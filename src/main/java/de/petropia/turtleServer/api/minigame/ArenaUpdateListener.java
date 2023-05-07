package de.petropia.turtleServer.api.minigame;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.petropia.turtleServer.server.cloudNet.CloudNetAdapter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class ArenaUpdateListener {
    private static class ArenaUpdateChannelListener {
        @EventListener
        public void onChannelMessage(ChannelMessageReceiveEvent event) {
            if (event.isQuery()) {
                return;
            }
            if (event.getChannel().equals(CloudNetAdapter.getArenaUpdateChannelName())) {
                return;
            }
            if(event.getMessage() == null){
                return;
            }
            if(event.getMessage().equals(CloudNetAdapter.getArenaDeleteMessage())){
                JsonDocument json = event.getChannelMessage().getJson();
                String arenaID = json.getString("id");
                String server = json.getString("server");
                Bukkit.getServer().getPluginManager().callEvent(new ArenaDeleteEvent(arenaID, server));
            }
            if(event.getMessage().equals(CloudNetAdapter.getArenaUpdateMessage())){
                JsonDocument json = event.getChannelMessage().getJson();
                String id = json.getString("id");
                String game = json.getString("game");
                GameMode mode = GameMode.valueOf(json.getString("type"));
                int maxPlayers = json.getInt("maxPlayerCount");
                int playerCount = json.getInt("playerCount");
                GameState state = GameState.valueOf(json.getString("gameState"));
                List<UUID> playerUUID = Stream.of(json.getString("playerUUIDs").split(" ")).map(UUID::fromString).toList();
                String server = json.getString("server");
                Bukkit.getServer().getPluginManager().callEvent(new ArenaUpateEvent(id, game, mode, maxPlayers, playerCount, state, playerUUID, server));
            }
            if(event.getMessage().equals(CloudNetAdapter.getArenaUpdateResendRequestMessage())){
                Bukkit.getServer().getPluginManager().callEvent(new ArenaUpdateResendRequestEvent());
            }
        }
    }
}