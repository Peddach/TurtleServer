package de.petropia.turtleServer.server.prefix.listener;

import de.petropia.turtleServer.server.prefix.PrefixManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    /**
     * Reset players prefix on leave
     * @param event {@link org.bukkit.event.player.PlayerJoinEvent}
     */
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event){
        PrefixManager.getInstance().removePlayerFromAllTeams(event.getPlayer());
    }
}
