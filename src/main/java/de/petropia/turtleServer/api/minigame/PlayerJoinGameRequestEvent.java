package de.petropia.turtleServer.api.minigame;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerJoinGameRequestEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final String id;
    private final UUID playerUUID;
    private boolean success = false;

    /**
     * Event is fired when another server "asks" this server to join a player. Success is by default false!
     * This can also be seen as a kind of annoncement in which arena a player wants to join when the PlayerJoinEvent is fired!
     * @param id Arena Id
     * @param playerUUID Player who wants to join uuid
     */
    public PlayerJoinGameRequestEvent(String id, UUID playerUUID) {
        this.id = id;
        this.playerUUID = playerUUID;
    }

    public boolean isSuccess(){
        return success;
    }

    public void setSuccess(boolean success){
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }
}
