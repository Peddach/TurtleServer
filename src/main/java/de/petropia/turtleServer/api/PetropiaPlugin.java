package de.petropia.turtleServer.api;

import de.petropia.turtleServer.api.util.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PetropiaPlugin extends JavaPlugin {

    //Create Message sender instance specific for the plugin
    private final MessageUtil messageUtil = new MessageUtil(this);

    /**
     * Get the plugin specific instance of {@link MessageUtil}
     * @return instance of {@link MessageUtil}
     */
    public MessageUtil getMessageSender(){
        return messageUtil;
    }
}
