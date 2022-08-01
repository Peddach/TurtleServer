package de.petropia.turtleServer.server.user.database.listener;

import de.dytanic.cloudnet.ext.bridge.bukkit.event.BukkitBridgeProxyPlayerDisconnectEvent;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import de.petropia.turtleServer.server.user.database.MongoDBHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerLeaveListener implements Listener {

    private static final List<UUID> RECENT_LEFT = new ArrayList<>();

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        RECENT_LEFT.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLater(TurtleServer.getInstance(), () -> {
            RECENT_LEFT.remove(event.getPlayer().getUniqueId());
        }, 30);
        TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(event.getPlayer().getUniqueId().toString()).thenAccept(petropiaPlayer -> {
            TurtleServer.getMongoDBHandler().unCachePlayer(petropiaPlayer);
        });
    }

    @EventHandler
    public void onPlayerNetworkLeft(BukkitBridgeProxyPlayerDisconnectEvent event){
        if(!RECENT_LEFT.contains(event.getNetworkConnectionInfo().getUniqueId())){
            return;
        }
        RECENT_LEFT.remove(event.getNetworkConnectionInfo().getUniqueId());
        TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(event.getNetworkConnectionInfo().getUniqueId().toString()).thenAccept(petropiaPlayer -> {
            petropiaPlayer.updateServer("null");
            petropiaPlayer.updateOnline(false);
            petropiaPlayer.updateLastOnline((int) Instant.now().getEpochSecond());
            petropiaPlayer.updatePlayer();
        });
    }
}
