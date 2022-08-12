package de.petropia.turtleServer.server;

import de.petropia.turtleServer.api.PetropiaPlugin;
import de.petropia.turtleServer.server.commands.PlayerCommand;
import de.petropia.turtleServer.server.prefix.PrefixManager;
import de.petropia.turtleServer.server.prefix.listener.AsyncChatListener;
import de.petropia.turtleServer.server.prefix.listener.LuckpermsGroupUpdateListener;
import de.petropia.turtleServer.server.prefix.listener.PlayerJoinListener;
import de.petropia.turtleServer.server.prefix.listener.PlayerLeaveListener;
import de.petropia.turtleServer.server.user.database.MongoDBHandler;
import de.petropia.turtleServer.server.user.database.listener.OnPlayerJoinListener;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.plugin.PluginManager;

public class TurtleServer extends PetropiaPlugin {

    private static TurtleServer plugin;

    private static MongoDBHandler mongoDBHandler;

    /**
     * @return The current instance of the {@link MongoDBHandler}
     */
    public static MongoDBHandler getMongoDBHandler() {
        return mongoDBHandler;
    }

    @Override
    protected void runOnEnableTasks() {
        plugin = this;
        saveDefaultConfig();    //save default config
        saveConfig();
        reloadConfig();
        new PrefixManager();    //init prefix manager
        mongoDBHandler = new MongoDBHandler();
    }

    @Override
    protected void runOnDisableTasks() {

    }

    @Override
    protected void registerCommands() {
        this.getCommand("player").setExecutor(new PlayerCommand());
        this.getCommand("player").setTabCompleter(new PlayerCommand());
    }

    @Override
    protected void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new AsyncChatListener(), this);
        manager.registerEvents(new PlayerJoinListener(), this);
        manager.registerEvents(new PlayerLeaveListener(), this);
        manager.registerEvents(new OnPlayerJoinListener(), this);
        manager.registerEvents(new de.petropia.turtleServer.server.user.database.listener.PlayerLeaveListener(), this);
        LuckPermsProvider.get().getEventBus().subscribe(UserDataRecalculateEvent.class, new LuckpermsGroupUpdateListener()::onGroupUpdate);
    }

    public static TurtleServer getInstance() {
        return plugin;
    }
}
