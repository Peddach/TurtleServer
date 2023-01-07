package de.petropia.turtleServer.api;

import de.petropia.turtleServer.api.arena.Arena;
import de.petropia.turtleServer.api.mysql.MinigameDatabase;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public abstract class PetropiaMinigame extends PetropiaPlugin{

    private static PetropiaMinigame plugin;
    private final MinigameDatabase minigameDatabase = new MinigameDatabase(this);
    private final List<Arena> arenas = new ArrayList<>();
    private final Hashtable<Player, Arena> playerArenas = new Hashtable<>();

    private int maxPlayers;
    private int requiredPlayersForStart;

    @Override
    public void onEnable() {
        super.onEnable();

        plugin = setPlugin();

        createConfigData();

        maxPlayers = getConfig().getInt("MaxPlayers");
        requiredPlayersForStart = getConfig().getInt("RequiredPlayersForStart");

        //Create necessary tables is the database
        minigameDatabase.deleteRemainingData();
        minigameDatabase.createArenasTable();
        minigameDatabase.createJoiningPlayersTable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for(Arena arena : arenas){
            arena.deleteArena();
        }
    }

    /**
     * Creates necessary config information it if it doesn't exist
     */
    protected void createConfigData(){
        //Database information
        if(!getConfig().contains("MinigameDatabase.Address")){
            getConfig().set("MinigameDatabase.Address", "localhost");
        }
        if(!getConfig().contains("MinigameDatabase.Port")){
            getConfig().set("MinigameDatabase.Port", "3306");
        }
        if(!getConfig().contains("MinigameDatabase.Name")){
            getConfig().set("MinigameDatabase.Name", "Minigame");
        }
        if(!getConfig().contains("MinigameDatabase.Username")){
            getConfig().set("MinigameDatabase.Username", "Minigame");
        }
        if(!getConfig().contains("MinigameDatabase.Password")){
            getConfig().set("MinigameDatabase.Password", "password");
        }

        //Arena information
        if(!getConfig().contains("ArenaCount")){
            getConfig().set("ArenaCount", 1);
        }

        //Player information
        if(!getConfig().contains("MaxPlayers")){
            getConfig().set("MaxPlayers", 1);
        }
        if(!getConfig().contains("RequiredPlayersForStart")){
            getConfig().set("RequiredPlayersForStart", 1);
        }

        saveConfig();
    }

    protected abstract PetropiaMinigame setPlugin();

    public static PetropiaMinigame getPlugin() {
        return plugin;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getRequiredPlayersForStart() {
        return requiredPlayersForStart;
    }

    public MinigameDatabase getMinigameDatabase() {
        return minigameDatabase;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public Hashtable<Player, Arena> getPlayerArenas() {
        return playerArenas;
    }
}
