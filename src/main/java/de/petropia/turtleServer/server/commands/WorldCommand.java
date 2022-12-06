package de.petropia.turtleServer.server.commands;

import de.petropia.turtleServer.api.worlds.WorldManager;
import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.TriState;
import net.lingala.zip4j.exception.ZipException;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
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
        if(args[0].equalsIgnoreCase("deleteLocal")){
            if(args.length != 2){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine Welt an!"));
                return false;
            }
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Bist du sicher, dass die Welt aus der Datenbank gelöscht werden soll?", NamedTextColor.RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Diese Aktion ist irreversibel!", NamedTextColor.DARK_RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("[✔]", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(
                    ClickEvent.Action.RUN_COMMAND, "/world deleteLocalyes " + args[1]
            )));
        }
        if(args[0].equalsIgnoreCase("deleteLocalyes")){
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Lösche Welt" + args[1]));
            WorldManager.deleteLocalWorld(Bukkit.getWorld(args[1]));
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
            World.Environment worldToSaveEnv = worldToSave.getEnvironment();
            ChunkGenerator worldToSaveGenerator = worldToSave.getGenerator();
            Location worldToSaveSpawn = worldToSave.getSpawnLocation();
            for (Player wPlayer : worldToSave.getPlayers()) {
                wPlayer.teleport(Bukkit.getWorld("world").getSpawnLocation());
            }
            try {
                WorldManager.saveToDBWorld(worldToSave).thenAccept(bool -> {
                    if(bool){
                        TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Welt erfolgreich gespeichert!", NamedTextColor.GREEN));
                    } else {
                        TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Fehler beim speichern der Welt!", NamedTextColor.RED));
                    }
                    Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> {
                        WorldCreator worldCreator = new WorldCreator(worldToSave.getName());
                        worldCreator.environment(worldToSaveEnv);
                        worldCreator.keepSpawnLoaded(TriState.FALSE);
                        worldCreator.generator(worldToSaveGenerator);
                        World newWorld = worldCreator.createWorld();
                        newWorld.setSpawnLocation(worldToSaveSpawn);
                        Bukkit.getScheduler().runTaskLater(TurtleServer.getInstance(), () -> {
                            Location newSpawn = worldToSaveSpawn.clone();
                            newSpawn.setWorld(Bukkit.getWorld(worldToSave.getName()));
                            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Du wirst zurück teleportiert!", NamedTextColor.GREEN));
                            newWorld.setSpawnLocation(newSpawn);
                            player.teleportAsync(newSpawn).exceptionally(throwable -> {
                                throwable.printStackTrace();
                                return null;
                            }).thenAccept(success -> {
                                if(!success){
                                    TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Async teleport failed! Teleport sync", NamedTextColor.RED));
                                    player.teleport(newSpawn);
                                    newWorld.setSpawnLocation(newSpawn);
                                }
                            });
                        }, 5*20);
                    });
                });
            } catch (ZipException e) {
                e.printStackTrace();
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Es ist ein Fehler aufgetreten! " + e.getMessage(), NamedTextColor.RED));
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
            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
        }
        return false;
    }
}
