package de.petropia.turtleServer.server.cloudNet.dto;

import de.petropia.turtleServer.api.minigame.GameMode;
import de.petropia.turtleServer.api.minigame.GameState;

public record PublishArenaUpdateDTO(
        String game,
        String id,
        GameMode mode,
        int maxPlayers,
        int playerCount,
        GameState state,
        String players,
        String server
) {}
