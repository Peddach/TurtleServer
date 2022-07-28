package de.petropia.turtleServer.server.prefix.listener;

import de.petropia.turtleServer.server.prefix.PrefixManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event){
        PrefixManager.getInstance().removePlayerFromAllTeams(event.getPlayer());
    }
}
