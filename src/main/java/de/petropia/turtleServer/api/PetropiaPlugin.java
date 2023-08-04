package de.petropia.turtleServer.api;

import de.petropia.turtleServer.api.util.MessageUtil;
import de.petropia.turtleServer.server.cloudNet.CloudNetAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PetropiaPlugin extends JavaPlugin {

    private final MessageUtil messageUtil = new MessageUtil(this);
    private final CloudNetAdapter cloudNetAdapter = new CloudNetAdapter();

    /**
     * @deprecated Only for already integrated plugins. Use {@link PetropiaPlugin#getMessageUtil()} instead
     * @return current instance for Plugin
     */
    @Deprecated
    public MessageUtil getMessageSender(){
        return messageUtil;
    }

    public MessageUtil getMessageUtil(){
        return messageUtil;
    }

    public CloudNetAdapter getCloudNetAdapter() {
        return cloudNetAdapter;
    }
}
