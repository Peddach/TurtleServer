package de.petropia.turtleServer.server.commands;

import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.commandBlocker.CommandBlocker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TurtleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (!player.hasPermission("TurtleServer.command")) {
            return false;
        }
        if (args.length == 0) {
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Es gibt folgende Subcommands: BlockedCommands"));
        }
        if (args[0].equalsIgnoreCase("blockedCommands")) {
            for (String cmd : CommandBlocker.getBlockedCommands().keySet()) {
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text(cmd).color(NamedTextColor.GRAY));
            }
            return true;
        }
        return false;
    }
}
