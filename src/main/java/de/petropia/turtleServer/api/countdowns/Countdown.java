package de.petropia.turtleServer.api.countdowns;

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
    protected Countdown(int startSeconds, boolean async) {
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
    protected Countdown(int startSeconds) {
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
    protected void runTasks(){
        if(seconds <= 0){
            onCountdownStop();
            stop();
        }
    }

    /**
     * Executed, when the countdown stops
     */
    protected void onCountdownStop(){

    }

    /**
     * Stops the countdown
     */
    private void stop(){
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
