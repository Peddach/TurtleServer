package de.petropia.turtleServer.api.minigame;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArenaDeleteEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final String id;
    private final String server;

    public ArenaDeleteEvent(String id, String server){
        this.id = id;
        this.server = server;
    }

    public String getId() {
        return id;
    }

    public String getServer() {
        return server;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
