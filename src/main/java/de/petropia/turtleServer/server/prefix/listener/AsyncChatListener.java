package de.petropia.turtleServer.server.prefix.listener;

import de.petropia.turtleServer.server.prefix.PrefixManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {

    /**
     * Listenen on Chat event and format the message right
     * @param event {@link AsyncChatEvent}
     */
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component msg = event.message();
        Component prefix = PrefixManager.getInstance().getPlayerNameColorAndPrefix(player);
        Component finalMessage = prefix.append(Component.text(": ").color(NamedTextColor.GRAY).append(msg));
        event.renderer((source, sourceDisplayName, message, viewer) -> finalMessage);
    }
}
