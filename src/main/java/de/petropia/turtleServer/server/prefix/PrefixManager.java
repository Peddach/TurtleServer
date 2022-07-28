package de.petropia.turtleServer.server.prefix;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class PrefixManager {

    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final List<PrefixGroup> prefixGroups = new ArrayList<>();

    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    private static PrefixManager instance;

    public PrefixManager(){
        instance = this;
        loadGroups();
    }

    private void loadGroups(){
        for(Group group : luckPerms.getGroupManager().getLoadedGroups()) {
            String minimessagePrefix = group.getCachedData().getMetaData().getPrefix();
            if(minimessagePrefix == null){
                minimessagePrefix = "<#d4a50b>Spieler <gray>| ";
            }
            Component prefix = MiniMessage.miniMessage().deserialize(minimessagePrefix);
            String rank = "zzzzz";
            if(group.getWeight().isPresent()){
                rank = weightAsCharacters(group.getWeight().getAsInt());
            }
            prefixGroups.add(new PrefixGroup(group.getName(), prefix, rank));
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            setDefaultPrefix(player);
        }
    }

    public void setDefaultPrefix(Player player){
        removePlayerFromAllTeams(player);
        PrefixGroup prefixGroup = getPrefixGroup(player);
        Team team = prefixGroup.getTeam();
        team.addPlayer(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(scoreboard);
        }
        player.playerListName(prefixGroup.getPrefix().append(player.name().color(NamedTextColor.WHITE)));
    }

    public PrefixGroup getPrefixGroup(Player player){
        for(PrefixGroup prefixGroup : prefixGroups){
            if (!luckPerms.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup().equals(prefixGroup.getName())){
                continue;
            }
            return prefixGroup;
        }
       return null;
    }

    public void removePlayerFromAllTeams(Player player) {
        for (Team team : this.scoreboard.getTeams()) {
            if (!team.getEntries().contains(player.getName())) {
                continue;
            }
            team.removePlayer(player);
        }
    }

    private String weightAsCharacters(int weight) {
        String[] abc = { "j", "i", "h", "g", "f", "e", "d", "c", "b", "a" };
        String[] input = String.valueOf(weight).split("");
        StringBuilder output = new StringBuilder();
        for (String s : input) {
            int n = Integer.parseInt(s);
            output.append(abc[n]);
        }
        return output.toString();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public static PrefixManager getInstance(){
        return instance;
    }

}
