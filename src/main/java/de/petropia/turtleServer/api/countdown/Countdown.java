package de.petropia.turtleServer.api.countdown;

import de.petropia.turtleServer.api.PetropiaMinigame;
import org.bukkit.Bukkit;

public abstract class Countdown {

    protected int startSeconds;
    protected int seconds;
    private final int taskID;

    /**
     * Tells bukkit to schedule a task, that repeats every second
     * @param startSeconds The amount of seconds, that the countdown starts with
     * @param async if {@code true}: runs the task asynchronously
     */
    public Countdown(int startSeconds, boolean async) {
        this.startSeconds = startSeconds;
        seconds = startSeconds;
        if(async) {
            taskID = PetropiaMinigame.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(PetropiaMinigame.getPlugin(), () -> {
                runTasks();
                seconds--;
            },0,20).getTaskId();
        }else{
            taskID = PetropiaMinigame.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(PetropiaMinigame.getPlugin(), () -> {
                runTasks();
                seconds--;
            },0,20);
        }
    }

    /**
     * Tells bukkit to schedule a task, that repeats every second
     * @param startSeconds The amount of seconds, that the countdown starts with
     */
    public Countdown(int startSeconds) {
        this.startSeconds = startSeconds;
        seconds = startSeconds;
        taskID = PetropiaMinigame.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(PetropiaMinigame.getPlugin(), () -> {
            runTasks();
            seconds--;
        },0,20);
    }

    /**
     * Gets executed every second
     */
    protected abstract void runTasks();

    /**
     * Stops the countdown
     */
    protected void stop(){
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
