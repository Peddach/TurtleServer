package de.petropia.turtleServer.server.prefix;

import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.Team;

public class PrefixGroup {

    private final Component prefix;
    private final String weight;
    private final Team team;
    private final String name;

    /**
     * Representation of a LuckpermsGroup but simplefied
     * @param name Name of the group
     * @param prefix The chat and tabprefix
     * @param weight weight as string
     */
    public PrefixGroup(String name, Component prefix, String weight){
        this.prefix = prefix;
        this.weight = weight;
        this.name = name;
        this.team = PrefixManager.getInstance().getScoreboard().registerNewTeam(weight + name);
        this.team.displayName(prefix);
        this.team.prefix(prefix);
    }

    /**
     * @return chat and tab prefix of the group
     */
    public Component getPrefix() {
        return prefix;
    }

    /**
     * @return Weight as string alphabetic representation
     */
    public String getWeight() {
        return weight;
    }

    /**
     * @return Scoreboard team of group
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @return Name of the group in LuckPerms
     */
    public String getName() {
        return name;
    }
}
