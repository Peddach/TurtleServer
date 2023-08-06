package de.petropia.turtleServer.api.minigame;

import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.cloudNet.CloudNetAdapter;
import de.petropia.turtleServer.server.cloudNet.dto.ArenaDeleteDTO;
import de.petropia.turtleServer.server.cloudNet.dto.PublishArenaUpdateDTO;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArenaUpdateListener {
        @EventListener
        public void onChannelMessage(ChannelMessageReceiveEvent event) {
            if (event.query()) {
                return;
            }
            if (!event.channel().equals(CloudNetAdapter.getArenaUpdateChannelName())) {
                return;
            }
            if(event.message().equals(CloudNetAdapter.getArenaUpdateResendRequestMessage())){
                Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new ArenaUpdateResendRequestEvent()));
                return;
            }
            if(event.message().equals(CloudNetAdapter.getArenaDeleteMessage())){
                ArenaDeleteDTO arenaDeleteDTO = event.channelMessage().content().readObject(ArenaDeleteDTO.class);
                Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new ArenaDeleteEvent(arenaDeleteDTO.id(), arenaDeleteDTO.server())));
            }
            if(event.message().equals(CloudNetAdapter.getArenaUpdateMessage())){
                PublishArenaUpdateDTO publishArenaUpdateDTO = event.channelMessage().content().readObject(PublishArenaUpdateDTO.class);
                String id = publishArenaUpdateDTO.id();
                String game = publishArenaUpdateDTO.game();
                GameMode mode = publishArenaUpdateDTO.mode();
                int maxPlayers = publishArenaUpdateDTO.maxPlayers();
                int playerCount = publishArenaUpdateDTO.playerCount();
                GameState state = publishArenaUpdateDTO.state();
                List<UUID> playerUUID = parsePlayerUUIDs(publishArenaUpdateDTO.players());
                String server = publishArenaUpdateDTO.server();
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