package de.petropia.turtleServer.server.commands;

import de.petropia.turtleServer.api.util.TimeUtil;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player bukkitPlayer)){
            return false;
        }
        if(!bukkitPlayer.hasPermission("TurtleServer.command.player")){
            bukkitPlayer.sendMessage(Component.text("Keine Rechte").color(NamedTextColor.RED));
            return false;
        }
        if(args.length == 0){
            bukkitPlayer.sendMessage(Component.text("Subcommands: Cache, Info"));
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("Cache")){
            sendMessage(bukkitPlayer, Component.text("Cached players"));
            for(PetropiaPlayer player : TurtleServer.getMongoDBHandler().getChachedPlayers()){
                printPlayerInfo(player, bukkitPlayer);
            }
            TurtleServer.getPlugin().getMessageUtil().sendMessage(bukkitPlayer, Component.text("Spieler im cache: " + TurtleServer.getMongoDBHandler().getChachedPlayers().size()));
        }
        if(args[0].equalsIgnoreCase("info")){
            if(args.length == 1){
                sendMessage(bukkitPlayer, Component.text("Bitte gib an, ob du die uuid angeben willst oder den Nutzernamen"));
                sendMessage(bukkitPlayer, Component.text("Bedenke, Nutzernamen ändern sich, UUIDs bleiben immer gleich"));
                return false;
            }
            if(args.length == 3 && args[1].equalsIgnoreCase("name")){
                TurtleServer.getMongoDBHandler().getPetropiaPlayerByUsername(args[2]).thenAccept(player -> {
                   if(player == null){
                       sendMessage(bukkitPlayer, Component.text("Spieler nicht gefunden: " + args[2]).color(NamedTextColor.RED));
                       return;
                   }
                   printPlayerInfo(player, bukkitPlayer);
                });
            }
            else if(args.length == 3 && args[1].equalsIgnoreCase("uuid")){
                TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(args[2]).thenAccept(player -> {
                    if(player == null){
                        sendMessage(bukkitPlayer, Component.text("Spieler nicht gefunden: " + args[2]).color(NamedTextColor.RED));
                        return;
                    }
                   printPlayerInfo(player, bukkitPlayer);
                });
            }
            else {
                TurtleServer.getPlugin().getMessageUtil().sendMessage(bukkitPlayer, Component.text("Bitte gib einen Namen oder eine uuid an!").color(NamedTextColor.RED));
            }
        }
        return false;
    }

    private void printPlayerInfo(PetropiaPlayer petropiaPlayer, Player sender){
        TurtleServer.getPlugin().getMessageUtil().sendMessage(sender, Component.text("ID: " + petropiaPlayer.getId().toHexString()));
        TurtleServer.getPlugin().getMessageUtil().sendMessage(sender, Component.text("UUID: " + petropiaPlayer.getUuid()));
        TurtleServer.getPlugin().getMessageUtil().sendMessage(sender, Component.text("NAME: " + petropiaPlayer.getUserName()));
        TurtleServer.getPlugin().getMessageUtil().sendMessage(sender, Component.text("ONLINE: " + petropiaPlayer.isOnline()));
        TurtleServer.getPlugin().getMessageUtil().sendMessage(sender, Component.text("SERVER: " + petropiaPlayer.getServer()));
        TurtleServer.getPlugin().getMessageUtil().sendMessage(sender, Component.text("LAST ONLINE: " + TimeUtil.unixTimestampToString(petropiaPlayer.getLastOnline())));
        TurtleServer.getPlugin().getMessageUtil().sendMessage(sender, Component.text("HISTORY: " + nameHistoryAsString(petropiaPlayer)));
    }

    private String nameHistoryAsString(PetropiaPlayer petropiaPlayer) {
        StringBuilder string = new StringBuilder();
        for(String name : petropiaPlayer.getNameHistory()){
            string.append(name).append(", ");
        }
        return string.toString();
    }

    private void sendMessage(CommandSender sender, Component message){
        if(sender instanceof Player player){
            TurtleServer.getPlugin().getMessageUtil().sendMessage(player, message);
            return;
        }
        sender.sendMessage(message);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("TurtleServer.command.player")){
            return null;
        }
        List<String> complete = new ArrayList<>();
        if(args.length == 1){
            StringUtil.copyPartialMatches(args[0], Arrays.asList("cache", "info"), complete);
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("info")){
            StringUtil.copyPartialMatches(args[1], Arrays.asList("uuid", "name"), complete);
        }
        if(args.length == 3 && args[1].equalsIgnoreCase("name")){
            List<String> players = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(p -> players.add(p.getName()));
            StringUtil.copyPartialMatches(args[2], players, complete);
        }
        Collections.sort(complete);
        return complete;
    }
}
