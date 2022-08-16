package de.petropia.turtleServer.api;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import de.petropia.turtleServer.api.arena.Arena;
import de.petropia.turtleServer.api.mysql.SQLDatabase;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public abstract class PetropiaMinigame extends PetropiaPlugin{

    private static PetropiaMinigame plugin;
    private final de.petropia.turtleServer.api.mysql.SQLDatabase SQLDatabase = new SQLDatabase(this);
    private final List<Arena> arenas = new ArrayList<>();
    private final Hashtable<Player, Arena> playerArenas = new Hashtable<>();
    private MVWorldManager worldManager;

    @Override
    protected void runOnEnableTasks() {
        plugin = setPlugin();

        createConfigData();

        SQLDatabase.connect();

        //Create necessary tables is the database
        SQLDatabase.deleteRemainingData();
        SQLDatabase.createArenasTable();
        SQLDatabase.createJoiningPlayersTable();

        MultiverseCore mvCore = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        worldManager = mvCore.getMVWorldManager();
    }

    @Override
    protected void runOnDisableTasks() {
        for(Arena arena : arenas){
            arena.deleteArena();
        }
        SQLDatabase.disconnect();
    }

    /**
     * Creates necessary config information it if it doesn't exist
     */
    protected void createConfigData(){
        //Database information
        if(!getConfig().contains("Database.Address")){
            getConfig().set("Database.Address", "localhost");
        }
        if(!getConfig().contains("Database.Port")){
            getConfig().set("Database.Port", "3306");
        }
        if(!getConfig().contains("Database.Name")){
            getConfig().set("Database.Name", "DeadByDaylight");
        }
        if(!getConfig().contains("Database.Username")){
            getConfig().set("Database.Username", "root");
        }
        if(!getConfig().contains("Database.Password")){
            getConfig().set("Database.Password", "");
        }

        //Arena information
        if(!getConfig().contains("ArenaCount")){
            getConfig().set("ArenaCount", 1);
        }

        saveConfig();
    }

    protected abstract PetropiaMinigame setPlugin();

    public static PetropiaMinigame getPlugin() {
        return plugin;
    }

    public de.petropia.turtleServer.api.mysql.SQLDatabase getSQLDatabase() {
        return SQLDatabase;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public Hashtable<Player, Arena> getPlayerArenas() {
        return playerArenas;
    }

    public MVWorldManager getWorldManager() {
        return worldManager;
    }
}
