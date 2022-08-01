package de.petropia.turtleServer.server.user.database.listener;

import com.destroystokyo.paper.profile.ProfileProperty;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import de.petropia.turtleServer.server.user.database.MongoDBHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;

public class OnPlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerLoginListener(AsyncPlayerPreLoginEvent event){
        PetropiaPlayer player = TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(event.getUniqueId().toString()).join();
        if(player != null){
            if(!event.getName().equals(player.getUserName())){  //Username update when necessary
                player.updateUserName(event.getName());
            }
            for (ProfileProperty profileProperty : event.getPlayerProfile().getProperties()) {  //Skin and signature update when necessary
                if (!profileProperty.getName().equalsIgnoreCase("textures")) {
                    continue;
                }
                if(player.getSkinTexture().equalsIgnoreCase(profileProperty.getValue())) {
                    break;
                }
                player.updateSkinTexture(profileProperty.getValue());
                player.updateSkinTextureSignature(profileProperty.getSignature());
                break;
            }
            player.updateServer(TurtleServer.getCloudNetAdapter().getServerInstanceName()); //Update Server
            player.updatePlayer();
            TurtleServer.getMongoDBHandler().cachePlayer(player);
            return;
        }
        player = new PetropiaPlayer();
        player.setUuid(event.getUniqueId().toString());
        for (ProfileProperty profileProperty : event.getPlayerProfile().getProperties()) {
            if (profileProperty.getName().equalsIgnoreCase("textures")) {
                player.updateSkinTexture(profileProperty.getValue());
                player.updateSkinTextureSignature(profileProperty.getSignature());
                break;
            }
        }
        player.updateOnline(true);
        player.updateServer(TurtleServer.getCloudNetAdapter().getServerInstanceName());
        player.updateLastOnline((int) Instant.now().getEpochSecond());
        player.updatePlayer();
        TurtleServer.getMongoDBHandler().cachePlayer(player);
    }
}
