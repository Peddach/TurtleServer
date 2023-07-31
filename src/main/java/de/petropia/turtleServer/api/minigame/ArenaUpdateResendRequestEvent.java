package de.petropia.turtleServer.api.minigame;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArenaUpdateResendRequestEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public ArenaUpdateResendRequestEvent(){}

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }


    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }
}
