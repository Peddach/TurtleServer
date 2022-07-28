package de.petropia.turtleServer.server.prefix;

import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.Team;

public class PrefixGroup {

    private final Component prefix;
    private final String weight;
    private final Team team;
    private final String name;

    public PrefixGroup(String name, Component prefix, String weight){
        this.prefix = prefix;
        this.weight = weight;
        this.name = name;
        this.team = PrefixManager.getInstance().getScoreboard().registerNewTeam(weight + name);
        this.team.displayName(prefix);
    }

    public Component getPrefix() {
        return prefix;
    }

    public String getWeight() {
        return weight;
    }

    public Team getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }
}
