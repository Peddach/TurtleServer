package de.petropia.turtleServer.server.prefix.listener;

import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.prefix.PrefixManager;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatPreviewListener implements Listener {

    @EventHandler
    public void onAsyncChatPreview(AsyncChatDecorateEvent event) {
        Player player = event.player();
        Component prefix = PrefixManager.getInstance().getPlayerNameColorAndPrefix(player);
        event.result(prefix.append(event.originalMessage()));
    }
}
