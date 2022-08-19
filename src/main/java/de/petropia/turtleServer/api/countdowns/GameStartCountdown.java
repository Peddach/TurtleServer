package de.petropia.turtleServer.api.countdowns;

import de.petropia.turtleServer.api.PetropiaMinigame;
import de.petropia.turtleServer.api.arena.Arena;
import de.petropia.turtleServer.api.arena.gamestate.GameState;
import net.kyori.adventure.text.Component;

public class GameStartCountdown extends Countdown {

    private final Arena arena;

    public GameStartCountdown(Arena arena, int startSeconds) {
        super(startSeconds);
        this.arena = arena;
    }

    @Override
    protected void runTasks() {
        super.runTasks();
        if(arena.getPlayers().size() >= PetropiaMinigame.getPlugin().getRequiredPlayersForStart()){
            switch (seconds) {
                case 60, 50, 40, 30, 20, 10, 5, 3, 2 ->
                        arena.broadcast(Component.text("Das Spiel startet in " + seconds + " Sekunden!"));
                case 1 -> arena.broadcast(Component.text("Das Spiel startet in einer Sekunde!"));
                case 0 -> {
                    arena.startGame();
                }
            }
        }else{
            if(seconds <= startSeconds){
                seconds = startSeconds + 10;
                arena.broadcast(Component.text("§cEs werden noch §6" + (PetropiaMinigame.getPlugin().getMaxPlayers() - arena.getPlayers().size()) + "§c Spieler benötigt, um das Spiel zu starten!"));
            }
        }
    }

}
