package de.petropia.turtleServer.api;

import de.petropia.turtleServer.api.util.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PetropiaPlugin extends JavaPlugin {

    //Create Message sender instance specific for the plugin
    private final MessageUtil messageUtil = new MessageUtil(this);

    /**
     * @deprecated Only for allready intedrated plugins. Use {@link PetropiaPlugin#getMessageUtil()} instead
     * @return current instance for Plugin
     */
    @Deprecated
    public MessageUtil getMessageSender(){
        return messageUtil;
    }

    /**
     * @return Current instance if MessageUtil
     */
    public MessageUtil getMessageUtil(){
        return messageUtil;
    }
}
