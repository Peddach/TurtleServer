package de.petropia.turtleServer.api.minigame;

import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.cloudNet.CloudNetAdapter;
import de.petropia.turtleServer.server.cloudNet.dto.JoinGameMessageDTO;
import de.petropia.turtleServer.server.cloudNet.dto.JoinGameMessageResponseDTO;
import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.network.buffer.DataBuf;

import java.util.UUID;

public class PlayerJoinGameRequestChannelListener {

    @EventListener
    public void onChannelMessage(ChannelMessageReceiveEvent event){
        if(!event.channel().equals(CloudNetAdapter.getPlayerJoinGameQueryChannel())){
            return;
        }
        if(!event.message().equals(CloudNetAdapter.getPlayerJoinGameQueryMessage())){
            return;
        }
        if(!event.query()){
            return;
        }
        JoinGameMessageDTO joinGameMessageDTO = event.content().readObject(JoinGameMessageDTO.class);

        String id = joinGameMessageDTO.id();
        UUID player = joinGameMessageDTO.uuid();
        boolean success = TurtleServer.getInstance().getCloudNetAdapter().getJoinRequestResolver().apply(id, player);
        JoinGameMessageResponseDTO joinGameMessageResponseDTO = new JoinGameMessageResponseDTO(success);
        event.queryResponse(ChannelMessage.buildResponseFor(event.channelMessage())
                .buffer(DataBuf.empty().writeObject(joinGameMessageResponseDTO))
                .build());
    }
}
