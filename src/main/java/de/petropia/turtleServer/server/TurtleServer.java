package de.petropia.turtleServer.server;

import org.bukkit.plugin.java.JavaPlugin;

public class TurtleServer extends JavaPlugin {

    private static TurtleServer instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        instance = this;
    }

    /**
     * @return current instance of the turtleServer plugin
     */
    public static TurtleServer getInstance() {
        return instance;
    }

}
