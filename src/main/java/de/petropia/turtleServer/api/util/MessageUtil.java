package de.petropia.turtleServer.api.util;

import de.petropia.turtleServer.api.PetropiaPlugin;
import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {

    private final Component prefix;

    /**
     * The MessageUtil is a central class to send messages and other communication stuff to player in the right format
     * @param plugin Plugin sending the message
     */
    public MessageUtil(@NotNull PetropiaPlugin plugin) {
        String pluginName = plugin.getDescription().getName();
        this.prefix = Component.text("[").color(NamedTextColor.GRAY)
                .append(Component.text(pluginName).color(NamedTextColor.GOLD))
                .append(Component.text("] ").color(NamedTextColor.GRAY));
    }

    /**
     * Broadcast a message to an audience.
     * @see Audience#audience(Audience...)
     * @param audience Audience to broadcast to
     * @param message Message to send to the audience
     */
    public void sendMessage(@NotNull Audience audience, @NotNull Component message) {
        audience.sendMessage(prefix.append(message));
    }

    public void sendMessage(Player player, Component message){
        player.sendMessage(prefix.append(message));
    }

    /**
     * Sends a title to a player
     * @param audience The player(s), that should receive the title
     * @param title The main title
     * @param subTitle The subtitle (below the main title)
     */
    public void sendTitle(@NotNull Audience audience, Component title, Component subTitle){
        audience.showTitle(Title.title(title, subTitle));
    }

    /**
     * Broadcast a message to an audience
     * @deprecated use {@link MessageUtil#sendMessage(Audience, Component)} instead
     * @param audience Audience to broadcast to
     * @param message Message to send to the audience
     */
    @Deprecated(forRemoval = true)
    public void broadcastMessage(@NotNull Audience audience, @NotNull Component message) {
        audience.sendMessage(prefix.append(message));
    }

    /**
     * Quick and dirty method to show a debug message to all admins
     * @param message Message as String
     */
    public void showDebugMessage(String message){
        showDebugMessage(Component.text(message).color(NamedTextColor.GRAY));
    }

    /**
     * More elegant way to show a debug message to all admins on the server with the permission "TurtleServer.ShowDebugMessage"
     * @param message Debug message as {@link Component}
     */
    public void showDebugMessage(Component message) {
        if (!TurtleServer.getInstance().getConfig().getBoolean("Debug.ShowDebugMessage")) {
            return;
        }
        List<Player> players = new ArrayList<>();
        Bukkit.getServer().getOnlinePlayers().forEach(p -> {
            if (!p.hasPermission("TurtleServer.debug")) {
                return;
            }
            players.add(p);
        });
        Audience audience = Audience.audience(players);
        audience.sendMessage(prefix.append(Component.text("[i] ").color(NamedTextColor.YELLOW)).append(message));
    }
}
