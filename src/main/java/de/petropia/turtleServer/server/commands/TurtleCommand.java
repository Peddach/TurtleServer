package de.petropia.turtleServer.server.commands;

import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.commandBlocker.CommandBlocker;
import de.petropia.turtleServer.server.prefix.PrefixManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
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
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Es gibt folgende Subcommands: BlockedCommands, NameOn, NameOff"));
            return false;
        }
        if (args[0].equalsIgnoreCase("blockedCommands")) {
            for (String cmd : CommandBlocker.getBlockedCommands().keySet()) {
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text(cmd).color(NamedTextColor.GRAY));
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("NameOff")){
            PrefixManager.getInstance().getPrefixGroups().forEach(prefixGroup -> prefixGroup.getTeam().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER));
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Nametags aus").color(NamedTextColor.GRAY));
        }
        if(args[0].equalsIgnoreCase("NameOn")){
            PrefixManager.getInstance().getPrefixGroups().forEach(prefixGroup -> prefixGroup.getTeam().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS));
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Nametags an").color(NamedTextColor.GRAY));
        }
        return false;
    }
}
