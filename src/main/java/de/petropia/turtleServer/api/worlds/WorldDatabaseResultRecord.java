package de.petropia.turtleServer.api.worlds;

import org.bukkit.World;

public record WorldDatabaseResultRecord(String id, byte[] data, World.Environment environment) {
}
