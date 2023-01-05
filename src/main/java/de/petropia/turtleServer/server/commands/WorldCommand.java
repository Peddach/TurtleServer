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
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WorldCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("TurtleServer.WorldCommand")) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (args.length < 1) {
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Argumente: copy, load, save", NamedTextColor.GRAY));
            return false;
        }
        if (args[0].equalsIgnoreCase("list")) {
            Component message = Component.text("Aktuell sind folgende Welten geladen: ", NamedTextColor.GRAY);
            for(int i = 0; i < Bukkit.getWorlds().size(); i++){
                World world = Bukkit.getWorlds().get(i);
                Component worldMsg = Component.text(world.getName(), NamedTextColor.GREEN)
                        .hoverEvent(Component.empty().toBuilder().append(
                                Component.text("Env: " + world.getEnvironment(), NamedTextColor.GRAY),
                                Component.newline(),
                                Component.text("Loaded Chunks: " + world.getLoadedChunks().length, NamedTextColor.GRAY),
                                Component.newline(),
                                Component.text("Players: " + world.getPlayers().size(), NamedTextColor.GRAY)).asComponent())
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/world tp " + world.getName()));
                message = message.append(worldMsg);
                if(i + 1 < Bukkit.getWorlds().size()){
                    message = message.append(Component.text(", ", NamedTextColor.GRAY));
                }
            }
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, message);
        }
        if(args[0].equalsIgnoreCase("tp")){
            if(args.length != 2){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine Welt an!", NamedTextColor.RED));
                return false;
            }
            World world = Bukkit.getWorld(args[1]);
            if(world == null){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Welte wurde nicht gefunden", NamedTextColor.RED));
                return false;
            }
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Du wirst gleich teleportiert!", NamedTextColor.GREEN));
            player.teleportAsync(world.getSpawnLocation());
        }
        if (args[0].equalsIgnoreCase("deleteLocal")) {
            if (args.length != 2) {
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine Welt an!", NamedTextColor.RED));
                return false;
            }
            if(Bukkit.getWorld(args[1]) == null){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Diese Welt existiert nicht!", NamedTextColor.RED));
                return false;
            }
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Bist du sicher, dass die Welt vom aktuellen Server gelöscht werden soll?", NamedTextColor.RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Diese Aktion ist irreversibel!", NamedTextColor.DARK_RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("[✔]", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(
                    ClickEvent.Action.RUN_COMMAND, "/world deleteLocalyes " + args[1]
            )));
        }
        if (args[0].equalsIgnoreCase("deleteLocalyes")) {
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Lösche Welt: " + args[1], NamedTextColor.RED));
            WorldManager.deleteLocalWorld(Bukkit.getWorld(args[1]));
        }
        if (args[0].equalsIgnoreCase("deleteDB")) {
            if (args.length != 2) {
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine Welt an!"));
                return false;
            }
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Bist du sicher, dass die Welt aus der Datenbank gelöscht werden soll?", NamedTextColor.RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Diese Aktion ist irreversibel!", NamedTextColor.DARK_RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("[✔]", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(
                    ClickEvent.Action.RUN_COMMAND, "/world deleteDByes " + args[1]
            )));
        }
        if (args[0].equalsIgnoreCase("deleteDByes")) {
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Lösche Welt aus Datenbank: " + args[1], NamedTextColor.RED));
            WorldManager.deleteDatabaseWorld(args[1]);
        }
        if (args[0].equalsIgnoreCase("save")) {
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Bist du dir sicher, dass die Welt in der du dich befindest gespeichert werden soll in der Datenbank?", NamedTextColor.RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("Diese Aktion ist irreversibel!", NamedTextColor.DARK_RED));
            TurtleServer.getInstance().getMessageUtil().sendMessage(sender, Component.text("[✔]", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(
                    ClickEvent.Action.RUN_COMMAND, "/world saveyes"
            )));
        }
        if (args[0].equalsIgnoreCase("saveyes")) {
            World worldToSave = player.getWorld();
            World.Environment worldToSaveEnv = worldToSave.getEnvironment();
            ChunkGenerator worldToSaveGenerator = worldToSave.getGenerator();
            Location worldToSaveSpawn = worldToSave.getSpawnLocation();
            for (Player wPlayer : worldToSave.getPlayers()) {
                wPlayer.teleport(Bukkit.getWorld("world").getSpawnLocation());
            }
            try {
                WorldManager.saveToDBWorld(worldToSave).thenAccept(bool -> {
                    if (bool) {
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
                                if (!success) {
                                    TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Async teleport failed! Teleport sync", NamedTextColor.RED));
                                    player.teleport(newSpawn);
                                    newWorld.setSpawnLocation(newSpawn);
                                }
                            });
                        }, 20);
                    });
                });
            } catch (ZipException e) {
                e.printStackTrace();
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Es ist ein Fehler aufgetreten! " + e.getMessage(), NamedTextColor.RED));
            }
        }
        if (args[0].equalsIgnoreCase("load")) {
            if (args.length != 3) {
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
        if(args[0].equalsIgnoreCase("copylocal")){
            if(args.length != 3){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine Welt und dessen neuen Namen an!", NamedTextColor.RED));
                return false;
            }
            World world = Bukkit.getWorld(args[1]);
            if(world == null){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Die angegebene Welt existiert nicht!", NamedTextColor.RED));
                return false;
            }
            TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Kopiere Welt!", NamedTextColor.GREEN));
            WorldManager.copyLocalWorld(world, args[2]).thenAccept(newWorld -> player.teleportAsync(newWorld.getSpawnLocation())).exceptionally(e -> {
                e.printStackTrace();
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Fehler beim kopieren: " + e.getMessage(), NamedTextColor.RED));
                return null;
            });
        }
        if(args[0].equalsIgnoreCase("generate")){
            if(args.length != 3){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine Welt und die distanz vom Punkt 0, 0 in jede Richtung an", NamedTextColor.RED));
                return false;
            }
            World world = Bukkit.getWorld(args[1]);
            if(world == null){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Diese Welt existiert nicht!", NamedTextColor.RED));
                return false;
            }
            int blocks;
            try {
                blocks = Integer.parseInt(args[2]);
                if(blocks <= 0){
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Keine Valide Zahl!", NamedTextColor.RED));
                return false;
            }
            WorldManager.generate(world, blocks, false).thenAccept(success -> {
                if(success){
                    TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Chunks erfolgreich generiert!", NamedTextColor.GREEN));
                    return;
                }
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Chunks konnten nicht generiert werden!", NamedTextColor.RED));
            });
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList("save", "deleteLocal", "load", "list", "tp", "deleteDB", "generate", "copylocal"), tab);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("deleteLocal") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("generate") || args[0].equalsIgnoreCase("copylocal"))) {
            StringUtil.copyPartialMatches(args[1], Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()), tab);
        }
        return tab;
    }
}
