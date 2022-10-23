package de.petropia.turtleServer.server.user.database.listener;

import com.destroystokyo.paper.profile.ProfileProperty;
import de.dytanic.cloudnet.ext.bridge.bukkit.event.BukkitBridgeProxyPlayerLoginSuccessEvent;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;

public class OnPlayerJoinListener implements Listener {

    /**
     * Handles player create, load and cache logic on normal or first join
     * @param event The login event
     */
    @EventHandler
    public void onPlayerLoginListener(AsyncPlayerPreLoginEvent event){
        PetropiaPlayer player = TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(event.getUniqueId().toString()).join();
        if(player != null){ //Player exists -> update skin, name, server, online status
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
            player.updateServer(TurtleServer.getInstance().getCloudNetAdapter().getServerInstanceName()); //Update Server
            player.updateOnline(true); //Set player online
            player.updatePlayer().thenAccept(petropiaPlayer -> TurtleServer.getMongoDBHandler().cachePlayer(petropiaPlayer));
            return;
        }
        player = new PetropiaPlayer();  //Player not in db -> Create and init the player
        player.setUuid(event.getUniqueId().toString()); //UUID
        for (ProfileProperty profileProperty : event.getPlayerProfile().getProperties()) {  //Skin stuff
            if (profileProperty.getName().equalsIgnoreCase("textures")) {
                player.updateSkinTexture(profileProperty.getValue());
                player.updateSkinTextureSignature(profileProperty.getSignature());
                break;
            }
        }
        player.updateOnline(true);  //Online status
        player.updateFriendJoinMessage(true); //By default, every player should get a message when a friend joins
        player.updateServer(TurtleServer.getInstance().getCloudNetAdapter().getServerInstanceName()); //server name
        player.updateLastOnline((int) Instant.now().getEpochSecond());  //current time as last logout 'cause can't predict when player is going to log out
        player.updateUserName(event.getName()); //Add username
        player.updatePlayer().thenAccept(petropiaPlayer -> TurtleServer.getMongoDBHandler().cachePlayer(petropiaPlayer));
    }

    @EventHandler
    public void onPlayerNetworkJoin(BukkitBridgeProxyPlayerLoginSuccessEvent event){
        TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(event.getNetworkConnectionInfo().getUniqueId().toString()).thenAccept(petropiaPlayer -> {
            petropiaPlayer.updateLastOnline((int) Instant.now().getEpochSecond());
        });
    }
}
