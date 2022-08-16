package de.petropia.turtleServer.api.mysql;

import de.petropia.turtleServer.api.PetropiaMinigame;
import de.petropia.turtleServer.api.PetropiaPlugin;
import de.petropia.turtleServer.api.arena.Arena;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLDatabase {

    private final PetropiaPlugin plugin;
    private Connection con;
    private boolean isConnected;

    public SQLDatabase(PetropiaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Connects to the database
     */
    public void connect(){
        String address = plugin.getConfig().getString("Database.Address");
        String port = plugin.getConfig().getString("Database.Port");
        String database = plugin.getConfig().getString("Database.Name");
        String username = plugin.getConfig().getString("Database.Username");
        String password = plugin.getConfig().getString("Database.Password");

        if(!isConnected){
            try{
                con = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + database + "?autoReconnect=true", username, password);
                isConnected = true;
                plugin.getMessageUtil().showDebugMessage("§bSuccessfully connected to database!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Disconnects from the database
     */
    public void disconnect(){
        if(isConnected){
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates the table, that contains all arenas and their values in the database
     */
    public void createArenasTable(){
        try{
            con.prepareStatement("CREATE TABLE IF NOT EXISTS arenas (server VARCHAR(100), name VARCHAR(100), state VARCHAR(100), playerCount INT(16))").executeUpdate();
            plugin.getMessageUtil().showDebugMessage("§bSuccessfully created arena-table!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the table, that contains the arena, a joining player should be put in the database
     */
    public void createJoiningPlayersTable(){
        try{
            con.prepareStatement("CREATE TABLE IF NOT EXISTS joiningPlayers (server VARCHAR(100), name VARCHAR(100), arena VARCHAR(100))").executeUpdate();
            plugin.getMessageUtil().showDebugMessage("§bSuccessfully created joiningPlayers-table!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an arena to the database
     * @param arena the arena to add
     */
    public void updateArena(Arena arena){
        if(plugin instanceof PetropiaMinigame minigame) {
            if (!minigame.getArenas().contains(arena)) {
                Bukkit.getScheduler().runTaskAsynchronously(minigame, () -> {
                    try {
                        PreparedStatement st = con.prepareStatement("INSERT INTO arenas (server, name, state, playerCount) VALUES (?,?,?,?)");
                        st.setString(1, plugin.getCloudNetAdapter().getServerInstanceName());
                        st.setString(2, arena.getName());
                        st.setString(3, arena.getState().toString());
                        st.setInt(4, arena.getPlayers().size());
                        st.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(minigame, () -> {
                    try {
                        PreparedStatement st = con.prepareStatement("UPDATE arenas SET server = ?, state = ?, playerCount = ? WHERE name = ?");
                        st.setString(4, arena.getName());
                        st.setString(1, plugin.getCloudNetAdapter().getServerInstanceName());
                        st.setString(2, arena.getState().toString());
                        st.setInt(3, arena.getPlayers().size());
                        st.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    /**
     * Deletes an arena from database
     * @param arena the arena to delete
     */
    public void deleteArena(Arena arena){
        try {
            PreparedStatement st = con.prepareStatement("DELETE FROM arenas WHERE server = ? AND name = ?");
            st.setString(1, plugin.getCloudNetAdapter().getServerInstanceName());
            st.setString(2, arena.getName());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Should be executed asynchronously to avoid lags
     * @param player The player that joins the server
     * @return The arena that the joining player should be put in
     */
    public Arena getJoiningPlayerArena(PetropiaMinigame minigame, Player player){
        try {
            PreparedStatement st = con.prepareStatement("SELECT arena FROM joiningPlayers WHERE server = ? AND name = ?");
            st.setString(1, plugin.getCloudNetAdapter().getServerInstanceName());
            st.setString(2, player.getName());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                for (Arena arena : minigame.getArenas()) {
                    if (arena.getName().equals(rs.getString("arena"))) {
                        return arena;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a player to the joining-player-list
     * @param player The player to add
     */
    public void addJoiningPlayer(Player player, int maxPlayersInArena){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String[] arenaData = findArena(maxPlayersInArena);

            if (arenaData == null){
                plugin.getMessageUtil().sendMessage(player, Component.text("§cEs konnte keine freie Arena gefunden werden! Bitte kontaktiere das Team."));
                return;
            }

            String arenaServer = arenaData[0];
            String arenaName = arenaData[1];

            plugin.getMessageUtil().sendMessage(player, Component.text("Connecting..."));

            try{
                PreparedStatement st = con.prepareStatement("INSERT INTO joiningPlayers (server, name, arena) VALUES (?, ?, ?)");
                st.setString(1, arenaServer);
                st.setString(2, player.getName());
                st.setString(3, arenaName);
                st.executeUpdate();
            } catch (SQLException e){
                e.printStackTrace();
            }

            plugin.getCloudNetAdapter().sendPlayerToServer(player, arenaServer);
        });
    }

    /**
     * Removes a player from the joining-player-list
     * @param player The player to remove
     */
    public void removeJoiningPlayer(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try{
                PreparedStatement st = con.prepareStatement("DELETE FROM joiningPlayers WHERE name = ?");
                st.setString(1, player.getName());
                st.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Deletes remaining data, that might not have been deleted, when the server was closed
     */
    public void deleteRemainingData(){
        try {
            PreparedStatement st1 = con.prepareStatement("DELETE FROM arenas WHERE server = ?");
            PreparedStatement st2 = con.prepareStatement("DELETE FROM joiningPlayers WHERE server = ?");

            st1.setString(1, plugin.getCloudNetAdapter().getServerInstanceName());
            st2.setString(1, plugin.getCloudNetAdapter().getServerInstanceName());

            st1.executeUpdate();
            st2.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Finds an arena in the database, that a player can join. Should be executed asynchronously
     * @param maxPlayersInArena The maximum amount of players allowed in an arena
     * @return An array, that contains the arena's server and name
     */
    private String[] findArena(int maxPlayersInArena){
        List<String[]> arenaDataArrays = new ArrayList<>();

        //Get Arenas from database
        try{
            PreparedStatement st = con.prepareStatement("SELECT * FROM arenas");
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                String[] arenaData = new String[4];
                arenaData[0] = rs.getString("server");
                arenaData[1] = rs.getString("name");
                arenaData[2] = rs.getString("state");
                arenaData[3] = String.valueOf(rs.getInt("playerCount"));
                arenaDataArrays.add(arenaData);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        //Determine suitable arena for join
        for(String[] arenaData : arenaDataArrays){
            if(arenaData[2].equals("STARTING") && Integer.parseInt(arenaData[3]) < maxPlayersInArena){
                return new String[]{arenaData[0], arenaData[1]};
            }
        }

        return null;
    }
}
