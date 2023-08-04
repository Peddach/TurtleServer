package de.petropia.turtleServer.server;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.petropia.turtleServer.api.PetropiaPlugin;
import de.petropia.turtleServer.api.chatInput.ChatInputListener;
import de.petropia.turtleServer.api.minigame.ArenaUpdateListener;
import de.petropia.turtleServer.api.minigame.PlayerJoinGameRequestChannelListener;
import de.petropia.turtleServer.api.worlds.UserChangeWorldListener;
import de.petropia.turtleServer.api.worlds.WorldDatabase;
import de.petropia.turtleServer.api.worlds.WorldManager;
import de.petropia.turtleServer.server.commandBlocker.CommandBlocker;
import de.petropia.turtleServer.server.commands.PlayerCommand;
import de.petropia.turtleServer.server.commands.StatsCommand;
import de.petropia.turtleServer.server.commands.TurtleCommand;
import de.petropia.turtleServer.server.commands.WorldCommand;
import de.petropia.turtleServer.server.prefix.PrefixManager;
import de.petropia.turtleServer.server.prefix.listener.AsyncChatListener;
import de.petropia.turtleServer.server.prefix.listener.LuckpermsGroupUpdateListener;
import de.petropia.turtleServer.server.prefix.listener.PlayerJoinListener;
import de.petropia.turtleServer.server.prefix.listener.PlayerLeaveListener;
import de.petropia.turtleServer.server.user.database.MongoDBHandler;
import de.petropia.turtleServer.server.user.database.listener.OnPlayerJoinListener;
import de.petropia.turtleServer.server.user.database.listener.ServerShutdownListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    public void onEnable() {    //Run on Startup
        super.onEnable();
        plugin = this;
        saveDefaultConfig();    //save default config
        saveConfig();
        reloadConfig();
        mongoDBHandler = new MongoDBHandler();
        CommandBlocker.loadCommandBlockList();
        WorldDatabase.connect();
        registerListeners();
        registerCommands();
        WorldManager.loadSpawnWorld();
        Bukkit.getScheduler().runTask(this, PrefixManager::new);    //init Prefixmanager on POSTWORLD loading (See https://www.spigotmc.org/wiki/plugin-yml/#optional-attributes at load)
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void registerCommands() {
        this.getCommand("player").setExecutor(new PlayerCommand());
        this.getCommand("turtle").setExecutor(new TurtleCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("world").setExecutor(new WorldCommand());
        this.getCommand("player").setTabCompleter(new PlayerCommand());
        this.getCommand("world").setTabCompleter(new WorldCommand());
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new AsyncChatListener(), this);
        manager.registerEvents(new PlayerJoinListener(), this);
        manager.registerEvents(new PlayerLeaveListener(), this);
        manager.registerEvents(new OnPlayerJoinListener(), this);
        manager.registerEvents(new CommandBlocker(), this);
        manager.registerEvents(new ChatInputListener(), this);
        manager.registerEvents(new ServerShutdownListener(), this);
        manager.registerEvents(new UserChangeWorldListener(), this);
        manager.registerEvents(new de.petropia.turtleServer.server.user.database.listener.PlayerLeaveListener(), this);
        CloudNetDriver.getInstance().getEventManager().registerListeners(new ArenaUpdateListener());
        CloudNetDriver.getInstance().getEventManager().registerListeners(new PlayerJoinGameRequestChannelListener());
        LuckPermsProvider.get().getEventBus().subscribe(UserDataRecalculateEvent.class, new LuckpermsGroupUpdateListener()::onGroupUpdate);
    }

    /**
     * Shuts down the server properly. You may not call {@link Bukkit#shutdown()}, because player data would not be saved properly
     */
    public void shutdownServer(){
        ServerShutdownListener.lockServer();
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            player.kick(Component.text("Der Server auf dem du dich befunden hast wurde gestoppt!").color(NamedTextColor.RED));
        }
        Bukkit.getScheduler().runTaskLater(this, () -> {
            getLogger().warning("PetropiaPlayer data saved! Cache:" + mongoDBHandler.getChachedPlayers().size());
            Bukkit.getServer().shutdown();
        }, 30);

    }

    public static TurtleServer getInstance() {
        return plugin;
    }
}
