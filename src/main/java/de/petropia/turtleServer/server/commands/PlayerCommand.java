package de.petropia.turtleServer.server.commands;

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

public class PlayerCommand implements CommandExecutor, @Nullable TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("TurtleServer.command.player")){
            sender.sendMessage(Component.text("Keine Rechte").color(NamedTextColor.RED));
            return false;
        }
        if(args.length == 0){
            sender.sendMessage(Component.text("Subcommands: Cache, Info"));
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("Cache")){
            sendMessage(sender, Component.text("Cached players"));
            for(PetropiaPlayer player : TurtleServer.getMongoDBHandler().getChachedPlayers()){
                sendMessage(sender, Component.text("ID: " + player.getId()
                        + " UUID: " + player.getUuid() +
                        " Name: " + player.getUserName() +
                        " Online: " + player.isOnline() +
                        " LastLogout: " + player.getLastOnline() +
                        " Server: " + player.getServer() +
                        " SkinTexture: " + player.getSkinTexture() +
                        " SkinSig: " + player.getSkinTextureSignature() +
                        " NamesHistory: " + nameHistoryAsString(player)));
            }
        }
        if(args[0].equalsIgnoreCase("info")){
            if(args.length == 1){
                sendMessage(sender, Component.text("Bitte gib einen Spieler an!"));
                return false;
            }
            if(args.length == 2){
                TurtleServer.getMongoDBHandler().getPetropiaPlayerByUsername(args[1]).thenAccept(player -> {
                   if(player == null){
                       sendMessage(sender, Component.text("Spieler nicht gefunden").color(NamedTextColor.RED));
                       return;
                   }
                    sendMessage(sender, Component.text(
                            "ID: " + player.getId() +
                            " UUID: " + player.getUuid() +
                            " Name: " + player.getUserName() +
                            " Online: " + player.isOnline() +
                            " LastLogout: " + player.getLastOnline() +
                            " Server: " + player.getServer() +
                            " SkinTexture: " + player.getSkinTexture() +
                            " SkinSig: " + player.getSkinTextureSignature() +
                            " NamesHistory: " + nameHistoryAsString(player)));
                });
            }
        }
        return false;
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
            TurtleServer.getInstance().getMessageSender().sendMessage(player, message);
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
            List<String> players = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(p -> {players.add(p.getName());});
            StringUtil.copyPartialMatches(args[0], players, complete);
        }
        Collections.sort(complete);
        return complete;
    }
}
