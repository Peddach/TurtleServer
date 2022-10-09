package de.petropia.turtleServer.api.countdowns;

import de.petropia.turtleServer.api.arena.Arena;
import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class ShutdownArenaCountdown <T extends Arena> extends Countdown{

    private final Arena arena;
    private final Class<T> newArenaClass;

    public ShutdownArenaCountdown(int startSeconds, Arena arena, Class<T> newArenaClass) {
        super(startSeconds);
        this.arena = arena;
        this.newArenaClass = newArenaClass;
    }

    @Override
    protected void runTasks() {
        super.runTasks();
        switch (seconds) {
            case 30, 20, 10, 5, 3, 2 ->
                    TurtleServer.getInstance().getMessageUtil().sendMessage(Audience.audience(arena.getPlayers()), Component.text("Der Server startet in " + seconds + " Sekunden neu!"));
            case 1 ->
                    TurtleServer.getInstance().getMessageUtil().sendMessage(Audience.audience(arena.getPlayers()), Component.text("Der Server startet in einer Sekunde neu!"));
        }
    }

    @Override
    protected void onCountdownStop() {
        arena.deleteArena();
        try{
            newArenaClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException | RuntimeException ex){
            ex.printStackTrace();
        }
    }
}
