package de.petropia.turtleServer.server.prefix;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrefixManager {

    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final List<PrefixGroup> prefixGroups = new ArrayList<>();
    private final HashMap<Player, TextColor> playerNameColorMap = new HashMap<>();

    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    private static PrefixManager instance;

    /**
     * The PrefixManager ist for managing chat and tab prefixes on every server. By default, every prefix is loaded in the MiniMessage format from Luckperms
     */
    public PrefixManager(){
        instance = this;
        loadGroups();
    }

    /**
     * (Re-)Load all groups from luckperms and set default prefix for every player
     */
    private void loadGroups(){
        prefixGroups.clear();
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

    /**
     * (Re-)set the default prefix from Luckperms
     * @param player Player to set prefix
     */
    public void setDefaultPrefix(Player player) {
        removePlayerFromAllTeams(player);
        PrefixGroup prefixGroup = getPrefixGroup(player);
        Team team = prefixGroup.getTeam();
        team.addPlayer(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(scoreboard);
        }
        player.playerListName(prefixGroup.getPrefix().append(player.name().color(NamedTextColor.WHITE)));
        player.displayName(prefixGroup.getPrefix().append(player.name().color(NamedTextColor.WHITE)));
        player.customName(prefixGroup.getPrefix().append(player.name().color(NamedTextColor.WHITE)));
    }

    /**
     * Get the {@link PrefixGroup} for the corrosponding player
     * @param player Playe to get the group for
     * @return PrefixGroup of the player
     */
    public PrefixGroup getPrefixGroup(Player player){
        for(PrefixGroup prefixGroup : prefixGroups){
            if (!luckPerms.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup().equals(prefixGroup.getName())){
                continue;
            }
            return prefixGroup;
        }
       return null;
    }

    /**
     * Remove a player from all teams and effectively reset the prefix
     * @param player Player to remove
     */

    public void removePlayerFromAllTeams(Player player) {
        for (Team team : this.scoreboard.getTeams()) {
            if (!team.getEntries().contains(player.getName())) {
                continue;
            }
            team.removePlayer(player);
        }
        player.displayName(player.name());
        player.playerListName(player.name());
        player.customName(null);
    }

    /**
     * Set a special color for a player name
     * @param player Player for the color
     * @param color {@link TextColor} for the player
     */
    public void setPlayerNameColor(TextColor color, Player player){
        playerNameColorMap.put(player, color);
        PrefixGroup group = getPrefixGroup(player);
        player.playerListName(group.getPrefix().append(player.name().color(color)));
        player.displayName(group.getPrefix().append(player.name().color(color)));
        player.customName(group.getPrefix().append(player.name().color(color)));
    }

    /**
     * Get the prefix and the playername with players special color or, when color not specified, default name color
     * @param player Player
     * @return formatted prefix with player name
     */
    public Component getPlayerNameColorAndPrefix(Player player){
        PrefixGroup group = getPrefixGroup(player);
        if(!playerNameColorMap.containsKey(player)){
            return group.getPrefix().append(player.name().color(NamedTextColor.WHITE));
        }
        return group.getPrefix().append(player.name().color(playerNameColorMap.get(player)));
    }

    /**
     * Reset player specific name color
     * @param player Player to reset
     */
    public void resetPlayerNameColor(Player player){
        playerNameColorMap.remove(player);
        setDefaultPrefix(player);
    }

    /**
     * This is necassary because Bukkit sorts the tablist alphabetic so every team's name is representing their weight
     * @param weight Weight of the group as int
     * @return Represenentation of the weigt as string
     */
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

    public List<PrefixGroup> getPrefixGroups(){
        return prefixGroups;
    }

}
