package de.petropia.turtleServer.server.commands;

import de.petropia.turtleServer.api.worlds.WorldManager;
import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.lingala.zip4j.exception.ZipException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("TurtleServer.WorldCommand")){
            return false;
        }
        if(!(sender instanceof Player player)){
            return false;
        }
        if(args.length < 1){
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Argumente: copy, load, save", NamedTextColor.GRAY));
            return false;
        }
        if(args[0].equalsIgnoreCase("copy")){
            //TODO implement!
        }
        if(args[0].equalsIgnoreCase("save")){
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Bist du dir sicher, dass die Welt in der du dich befindest gespeichert werden soll in der Datenbank?", NamedTextColor.RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Diese Aktion ist irreversibel!", NamedTextColor.DARK_RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("[✔]", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(
                    ClickEvent.Action.RUN_COMMAND, "/world saveyes"
            )));
        }
        if(args[0].equalsIgnoreCase("saveyes")){
            World worldToSave = player.getWorld();
            for (Player wPlayer : worldToSave.getPlayers()) {
                wPlayer.teleport(Bukkit.getWorld("world").getSpawnLocation());
            }
            try {
                WorldManager.saveToDBWorld(worldToSave);
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Welt gespeichert!"));
            } catch (ZipException e) {
                e.printStackTrace();
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Es ist ein Fehler aufgetreten! " + e.getMessage()));
            }
        }
        if(args[0].equalsIgnoreCase("load")){
            if(args.length != 3){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine id der Welt an und danach dessen Name wenn sie geladen wird!"));
                return false;
            }
            WorldManager.loadWorld(args[1], args[2]).thenAccept(world -> {
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Welt erfolgreich geladen!"));
                player.teleportAsync(world.getSpawnLocation());
            });
        }
        return false;
    }
}
