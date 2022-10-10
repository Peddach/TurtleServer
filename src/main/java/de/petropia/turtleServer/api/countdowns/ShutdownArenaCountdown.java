package de.petropia.turtleServer.api.countdowns;

import de.petropia.turtleServer.api.PetropiaMinigame;
import de.petropia.turtleServer.api.arena.Arena;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
                    PetropiaMinigame.getPlugin().getMessageUtil().sendMessage(Audience.audience(arena.getPlayers()), Component.text("Der Server startet in ", NamedTextColor.RED).append(Component.text(seconds, NamedTextColor.GOLD).append(Component.text(" Sekunden neu!", NamedTextColor.RED))));
            case 1 ->
                    PetropiaMinigame.getPlugin().getMessageUtil().sendMessage(Audience.audience(arena.getPlayers()), Component.text("Der Server startet in einer Sekunde neu!", NamedTextColor.RED));
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
