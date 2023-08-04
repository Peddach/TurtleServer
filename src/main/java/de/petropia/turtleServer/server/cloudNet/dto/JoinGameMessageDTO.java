package de.petropia.turtleServer.server.cloudNet.dto;

import java.util.UUID;

public record JoinGameMessageDTO(
        UUID uuid,
        String id
){}
