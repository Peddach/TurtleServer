package de.petropia.turtleServer.api;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import de.petropia.turtleServer.api.arena.Arena;
import de.petropia.turtleServer.api.mysql.Database;
import de.petropia.turtleServer.api.util.MessageUtil;
import de.petropia.turtleServer.server.cloudNet.CloudNetAdapter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public abstract class PetropiaPlugin extends JavaPlugin {

    private static PetropiaPlugin plugin;

    private final List<Arena> arenas = new ArrayList<>();
    private final Hashtable<Player, Arena> playerArenas = new Hashtable<>();
    private final MessageUtil messageUtil = new MessageUtil(this);
    private final Database database = new Database(this);
    private final CloudNetAdapter cloudNetAdapter = new CloudNetAdapter();

    private MVWorldManager worldManager;

    @Override
    public void onEnable() {
        plugin = setPlugin();

        createConfigData();
        database.connect();

        MultiverseCore mvCore = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        worldManager = mvCore.getMVWorldManager();

        runOnEnableTasks();
    }

    @Override
    public void onDisable() {
        runOnDisableTasks();
    }

    /**
     * Runs all tasks, that should be executed in the onEnable()
     */
    protected abstract void runOnEnableTasks();

    /**
     * Runs all tasks, that should be executed in the onDisable()
     */
    protected abstract void runOnDisableTasks();

    /**
     * Sets the instance of the plugin. Is implemented in the child class
     */
    protected abstract PetropiaPlugin setPlugin();

    /**
     * Creates necessary config information it if it doesn't exist
     */
    protected void createConfigData(){
        //Database information
        if(!getConfig().contains("DatabaseAddress")){
            getConfig().set("DatabaseAddress", "localhost");
        }
        if(!getConfig().contains("DatabasePort")){
            getConfig().set("DatabasePort", "3306");
        }
        if(!getConfig().contains("Database")){
            getConfig().set("Database", "Masterbuilders");
        }
        if(!getConfig().contains("DatabaseUsername")){
            getConfig().set("DatabaseUsername", "root");
        }
        if(!getConfig().contains("DatabasePassword")){
            getConfig().set("DatabasePassword", "");
        }

        //Arena information
        if(!getConfig().contains("ArenaCount")){
            getConfig().set("ArenaCount", 1);
        }

        saveConfig();
    }

    /**
     * @deprecated Only for already integrated plugins. Use {@link PetropiaPlugin#getMessageUtil()} instead
     * @return current instance for Plugin
     */
    @Deprecated
    public MessageUtil getMessageSender(){
        return messageUtil;
    }

    public static PetropiaPlugin getPlugin() {
        return plugin;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public Hashtable<Player, Arena> getPlayerArenas() {
        return playerArenas;
    }

    public MessageUtil getMessageUtil(){
        return messageUtil;
    }

    public Database getDatabase() {
        return database;
    }

    public MVWorldManager getWorldManager() {
        return worldManager;
    }

    public CloudNetAdapter getCloudNetAdapter() {
        return cloudNetAdapter;
    }
}
