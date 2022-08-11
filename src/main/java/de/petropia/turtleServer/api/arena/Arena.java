package de.petropia.turtleServer.api.arena;

import de.petropia.turtleServer.api.PetropiaPlugin;
import de.petropia.turtleServer.api.arena.gamestate.GameState;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Arena {

    protected final String name;
    protected final World world;
    protected final List<PetropiaPlayer> players = new ArrayList<>();

    protected GameState state;

    public Arena() {
        name = getRandomName();
        world = setWorld();
        state = GameState.STARTING;
        init();
        updateArena();
        PetropiaPlugin.getPlugin().getArenas().add(this);
    }

    /**
     * Sets the world of the arena. Is implemented in the child class
     */
    protected abstract World setWorld();
    protected abstract void init();

    public void updateArena(){
        PetropiaPlugin.getPlugin().getDatabase().updateArena(this);
    }

    /**
     * @return a random name for the arena
     */
    private String getRandomName(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public String getName() {
        return name;
    }

    public List<PetropiaPlayer> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }
}
