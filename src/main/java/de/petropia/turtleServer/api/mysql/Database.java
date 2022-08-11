package de.petropia.turtleServer.api.mysql;

import de.petropia.turtleServer.api.arena.Arena;
import org.bukkit.Bukkit;

import de.petropia.turtleServer.api.PetropiaPlugin;
import org.bukkit.entity.Player;

import java.sql.*;

public class Database {

    private final PetropiaPlugin plugin;
    private Connection con;
    private boolean isConnected;

    public Database(PetropiaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect(){
        String address = plugin.getConfig().getString("DatabaseAddress");
        String port = plugin.getConfig().getString("DatabasePort");
        String database = plugin.getConfig().getString("Database");
        String username = plugin.getConfig().getString("DatabaseUsername");
        String password = plugin.getConfig().getString("DatabasePassword");

        if(!isConnected){
            try{
                con = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + database + "?autoReconnect=true", username, password);
                isConnected = true;
                dropTables();
                plugin.getMessageUtil().showDebugMessage("§bSuccessfully connected to database!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates the table, that contains all arenas and their values in the database
     */
    public void createArenasTable(){
        try{
            con.prepareStatement("CREATE TABLE IF NOT EXISTS arenas (name VARCHAR(100), state VARCHAR(100), playerCount INT(16))").executeUpdate();
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
            con.prepareStatement("CREATE TABLE IF NOT EXISTS joiningPlayers (name VARCHAR(100), arena VARCHAR(100))").executeUpdate();
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
        if(!plugin.getArenas().contains(arena)){
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try{
                    PreparedStatement st = con.prepareStatement("INSERT INTO arenas (name, state, playerCount) VALUES (?,?,?)");
                    st.setString(1, arena.getName());
                    st.setString(2, arena.getState().toString());
                    st.setInt(3, arena.getPlayers().size());
                    st.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }else{
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try{
                    PreparedStatement st = con.prepareStatement("UPDATE arenas SET state = ?, playerCount = ? WHERE name = ?");
                    st.setString(3, arena.getName());
                    st.setString(1, arena.getState().toString());
                    st.setInt(2, arena.getPlayers().size());
                    st.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Deletes an arena from database
     * @param arena the arena to delete
     */
    public void deleteArena(Arena arena){
        try {
            PreparedStatement st = con.prepareStatement("DELETE FROM arenas WHERE name = ?");
            st.setString(1, arena.getName());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param player The player that joins the server
     * @return The arena that the joining player should be put in
     */
    public Arena getJoiningPlayerArena(Player player){
        Arena arena = null;
        try{
            PreparedStatement st = con.prepareStatement("SELECT arena FROM joiningPlayers WHERE name = ?");
            st.setString(1, player.getName());
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                for(Arena a : plugin.getArenas()){
                    if(a.getName().equals(rs.getString("arena"))){
                        arena = a;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arena;
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
     * Deletes the arena and joiningPlayers tables, in order to avoid database errors
     */
    private void dropTables(){
        try {
            PreparedStatement st = con.prepareStatement("DROP TABLES arenas, joiningPlayers");
            st.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
