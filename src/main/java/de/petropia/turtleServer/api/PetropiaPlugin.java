package de.petropia.turtleServer.api;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class PetropiaPlugin extends JavaPlugin {

    //Create Message sender instance specific for the plugin
    private final MessageSender messageSender = new MessageSender(this);

    /**
     * Get the plugin specific instance of {@link MessageSender}
     * @return instance of {@link MessageSender}
     */
    public MessageSender getMessageSender(){
        return messageSender;
    }
}
