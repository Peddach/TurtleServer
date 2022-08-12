package de.petropia.turtleServer.api;

import de.petropia.turtleServer.api.util.MessageUtil;
import de.petropia.turtleServer.server.cloudNet.CloudNetAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PetropiaPlugin extends JavaPlugin {

    private final MessageUtil messageUtil = new MessageUtil(this);
    private final CloudNetAdapter cloudNetAdapter = new CloudNetAdapter();

    @Override
    public void onEnable() {
        runOnEnableTasks();

        registerListeners();

        registerCommands();
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
     * Registers all event listeners
     */
    protected abstract void registerListeners();

    /**
     * Registers all commands
     */
    protected abstract void registerCommands();

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
