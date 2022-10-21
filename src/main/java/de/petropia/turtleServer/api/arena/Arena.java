package de.petropia.turtleServer.api.arena;

import de.petropia.turtleServer.api.PetropiaMinigame;
import de.petropia.turtleServer.api.arena.gamestate.GameState;
import de.petropia.turtleServer.api.countdowns.GameStartCountdown;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Arena {

    private final String name;
    protected final World world;
    private final List<Player> players = new ArrayList<>();
    private GameState state;
    private GameStartCountdown gameStartCountdown;

    public Arena() {
        name = getRandomName();
        world = setWorld();
        state = GameState.STARTING;
        init();
        updateArena();
        PetropiaMinigame.getPlugin().getArenas().add(this);
    }

    /**
     * Starts the game
     */
    public abstract void startGame();
    /**
     * Sets the world of the arena. Is implemented in the child class
     */
    protected abstract World setWorld();
    //Initializes the arena
    protected abstract void init();

    /**
     * Updates the arena in the database
     */
    public void updateArena(){
        PetropiaMinigame.getPlugin().getSQLDatabase().updateArena(this);
    }

    /**
     * Deletes the arena in the database
     */
    public void deleteArena(){
        for(Player player : players){
            PetropiaMinigame.getPlugin().getCloudNetAdapter().sendPlayerToLobby(player);
        }
        PetropiaMinigame.getPlugin().getWorldManager().deleteWorld(world.getName());
        PetropiaMinigame.getPlugin().getSQLDatabase().deleteArena(this);
        PetropiaMinigame.getPlugin().getArenas().remove(this);
    }

    /**
     * Sends a message to every player in this arena
     * @param message The message, that should be sent
     */
    public void broadcast(Component message){
        PetropiaMinigame.getPlugin().getMessageUtil().sendMessage(Audience.audience(players), message);
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

    public World getWorld() {
        return world;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public GameStartCountdown getGameStartCountdown() {
        return gameStartCountdown;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setGameStartCountdown(GameStartCountdown gameStartCountdown) {
        this.gameStartCountdown = gameStartCountdown;
    }
}
