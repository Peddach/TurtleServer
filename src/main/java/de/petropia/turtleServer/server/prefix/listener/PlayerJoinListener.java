package de.petropia.turtleServer.server.prefix.listener;

import de.petropia.turtleServer.server.prefix.PrefixManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PrefixManager.getInstance().setDefaultPrefix(event.getPlayer());
    }
}
