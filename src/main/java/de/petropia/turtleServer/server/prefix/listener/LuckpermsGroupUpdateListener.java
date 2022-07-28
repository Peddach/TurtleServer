package de.petropia.turtleServer.server.prefix.listener;

import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.prefix.PrefixManager;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LuckpermsGroupUpdateListener {

    public void onGroupUpdate(UserDataRecalculateEvent event) {
        Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> {
            Player player = Bukkit.getPlayer(event.getUser().getUniqueId());
            if (player != null)
                PrefixManager.getInstance().setDefaultPrefix(player);
        });
    }
}
