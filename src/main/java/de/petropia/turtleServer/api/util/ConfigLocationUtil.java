package de.petropia.turtleServer.api.util;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigLocationUtil {

    /**
     * Saves a location to a plugin config file
     * @param plugin The plugin, that needs a location saved in its config-file
     * @param loc The location, that should be saved to the plugin's config-file
     * @param path The config-path, where the location should be saved
     */
    public static void saveLocation(JavaPlugin plugin, Location loc, String path){
        plugin.getConfig().set(path + ".World", loc.getWorld().getName());
        plugin.getConfig().set(path + ".X", loc.getX());
        plugin.getConfig().set(path + ".Y", loc.getY());
        plugin.getConfig().set(path + ".Z", loc.getZ());
        plugin.getConfig().set(path + ".Yaw", loc.getYaw());
        plugin.getConfig().set(path + ".Pitch", loc.getPitch());
        plugin.saveConfig();
    }

    /**
     * @param plugin The plugin, that needs a location loaded from it's config-file
     * @param path The config-path, where the location is saved
     * @return A location, that was loaded from the plugins config-file
     */
    public static Location loadLocation(JavaPlugin plugin, String path){
        return new Location(
            plugin.getServer().getWorld(plugin.getConfig().getString(path + ".World")),
            plugin.getConfig().getDouble(path + ".X"),
            plugin.getConfig().getDouble(path + ".Y"),
            plugin.getConfig().getDouble(path + ".Z"),
            (float)plugin.getConfig().getDouble(path + ".Yaw"),
            (float)plugin.getConfig().getDouble(path + ".Pitch")
        );
    }

    /**
     * @param plugin The plugin, that needs a location loaded from it's config-file
     * @param path The config-path, where the location is saved
     * @return A location, that was loaded from the plugins config-file with int values for X,Y,Z
     */
    public static Location loadLocationInt(JavaPlugin plugin, String path){
        Location loc = loadLocation(plugin, path);
        return new Location(
                loc.getWorld(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ(),
                loc.getYaw(),
                loc.getPitch()
        );
    }

}
