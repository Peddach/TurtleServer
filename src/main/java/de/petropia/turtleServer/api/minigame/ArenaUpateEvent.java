package de.petropia.turtleServer.api.minigame;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ArenaUpateEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final String id, game, server;
    private final int playerCount, maxPlayerCount;
    private final GameState gameState;
    private final GameMode gameMode;

    public ArenaUpateEvent(String id, String game, GameMode mode, int playerCount, int maxPlayerCount, GameState state, List<UUID> playerUUIDs, String server){
        this.id = id;
        this.game = game;
        this.server = server;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.gameState = state;
        this.gameMode = mode;
    }

    public String getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public String getServer() {
        return server;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
