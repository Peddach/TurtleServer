package de.petropia.turtleServer.api.minigame;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.cloudNet.CloudNetAdapter;

import java.util.UUID;

public class PlayerJoinGameRequestChannelListener {

    @EventListener
    public void onChannelMessage(ChannelMessageReceiveEvent event){
        if(event.getMessage() == null){
            return;
        }
        if(!event.getChannel().equals(CloudNetAdapter.getPlayerJoinGameQueryChannel())){
            return;
        }
        if(!event.getMessage().equals(CloudNetAdapter.getPlayerJoinGameQueryMessage())){
            return;
        }
        if(!event.isQuery()){
            return;
        }
        if(event.getChannelMessage().getJson() == null || event.getChannelMessage().getJson().isEmpty()){
            return;
        }
        String id = event.getChannelMessage().getJson().getString("id");
        UUID player = UUID.fromString(event.getChannelMessage().getJson().getString("player"));
        boolean success = TurtleServer.getInstance().getCloudNetAdapter().getJoinRequestResolver().apply(id, player);
        event.setQueryResponse(ChannelMessage.buildResponseFor(event.getChannelMessage())
                .json(JsonDocument.newDocument()
                        .append("success", success))
                        .build());

    }
}
