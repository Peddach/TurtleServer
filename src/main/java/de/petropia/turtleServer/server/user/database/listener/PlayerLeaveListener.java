package de.petropia.turtleServer.server.user.database.listener;

import de.petropia.turtleServer.server.TurtleServer;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.modules.bridge.event.BridgeProxyPlayerDisconnectEvent;
import org.bukkit.Bukkit;
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
        Bukkit.getScheduler().runTaskLater(TurtleServer.getInstance(), () -> RECENT_LEFT.remove(event.getPlayer().getUniqueId()), 30);
        TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(event.getPlayer().getUniqueId().toString()).thenAccept(petropiaPlayer -> TurtleServer.getMongoDBHandler().unCachePlayer(petropiaPlayer));
    }

    @EventListener
    public void onPlayerNetworkLeft(BridgeProxyPlayerDisconnectEvent event){
        if(!RECENT_LEFT.contains(event.cloudPlayer().uniqueId())){
            return;
        }
        RECENT_LEFT.remove(event.cloudPlayer().uniqueId());
        TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(event.cloudPlayer().uniqueId().toString()).thenAccept(petropiaPlayer -> {
            petropiaPlayer.increateStats("General_Playtime", (int) Instant.now().getEpochSecond() - petropiaPlayer.getLastOnline());
            petropiaPlayer.updateServer("null");
            petropiaPlayer.updateOnline(false);
            petropiaPlayer.updateLastOnline((int) Instant.now().getEpochSecond());
            petropiaPlayer.updatePlayer();
        });
    }
}
