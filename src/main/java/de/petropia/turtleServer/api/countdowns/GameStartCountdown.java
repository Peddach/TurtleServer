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
        if(arena.getPlayers().size() >= 5){
            switch (seconds){
                case 60: case 50: case 40: case 30: case 20: case 10: case 5: case 3: case 2:
                    arena.broadcast(Component.text("Das Spiel startet in " + seconds + " Sekunden!"));
                    break;
                case 1:
                    arena.broadcast(Component.text("Das Spiel startet in einer Sekunde!"));
                    break;
                case 0:
                    arena.setState(GameState.INGAME);
                    //Init inGame-phase
                    arena.updateArena();
                    stop();
                    break;
            }
        }else{
            if(seconds <= startSeconds){
                seconds = startSeconds + 10;
                arena.broadcast(Component.text("§cEs werden noch §6" + (PetropiaMinigame.getPlugin().getMaxPlayers() - arena.getPlayers().size()) + "§c Spieler benötigt, um das Spiel zu starten!"));
            }
        }
    }

}
