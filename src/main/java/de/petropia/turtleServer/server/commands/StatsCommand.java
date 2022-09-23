package de.petropia.turtleServer.server.commands;

import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.stats.StatsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            return false;
        }
        TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(player.getUniqueId().toString()).thenAccept(StatsGui::new);
        return false;
    }
}
