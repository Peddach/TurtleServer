package de.petropia.turtleServer.api.minigame;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.cloudNet.CloudNetAdapter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArenaUpdateListener {
        @EventListener
        public void onChannelMessage(ChannelMessageReceiveEvent event) {
            if (event.isQuery()) {
                return;
            }
            if (!event.getChannel().equals(CloudNetAdapter.getArenaUpdateChannelName())) {
                return;
            }
            if(event.getMessage() == null){
                return;
            }
            if(event.getMessage().equals(CloudNetAdapter.getArenaUpdateResendRequestMessage())){
                Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new ArenaUpdateResendRequestEvent()));
                return;
            }
            if(event.getChannelMessage().getJson().isEmpty()){
                TurtleServer.getInstance().getLogger().warning("Recived Empty Arena Update Message from " + event.getSender().getName());
                return;
            }
            if(event.getMessage().equals(CloudNetAdapter.getArenaDeleteMessage())){
                JsonDocument json = event.getChannelMessage().getJson();
                String arenaID = json.getString("id");
                String server = json.getString("server");
                Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new ArenaDeleteEvent(arenaID, server)));
            }
            if(event.getMessage().equals(CloudNetAdapter.getArenaUpdateMessage())){
                JsonDocument json = event.getChannelMessage().getJson();
                String id = json.getString("id");
                String game = json.getString("game");
                GameMode mode = GameMode.valueOf(json.getString("type"));
                int maxPlayers = json.getInt("maxPlayerCount");
                int playerCount = json.getInt("playerCount");
                GameState state = GameState.valueOf(json.getString("gameState"));
                List<UUID> playerUUID = parsePlayerUUIDs(json.getString("playerUUIDs"));
                String server = json.getString("server");
                Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new ArenaUpateEvent(id, game, mode, playerCount, maxPlayers, state, playerUUID, server)));
            }
        }

    private List<UUID> parsePlayerUUIDs(String string){
        List<UUID> list = new ArrayList<>();
        if(string.isEmpty()){
            return list;
        }
        String[] split = string.split(",");
        for(String str : split){
            if(str.isEmpty() || str.isBlank()){
                continue;
            }
            list.add(UUID.fromString(str));
        }
        return list;
    }
}