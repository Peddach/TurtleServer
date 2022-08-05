package de.petropia.turtleServer.api.countdown;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Countdown {

    protected int startSeconds;
    protected int seconds;
    private final int taskID;

    /**
     * Tells bukkit to schedule a task, that repeats every second
     * @param plugin The plugin, that schedules the task
     * @param startSeconds The amount of seconds, that the countdown starts with
     * @param async if {@code true}: runs the task asynchronously
     */
    public Countdown(JavaPlugin plugin, int startSeconds, boolean async) {
        this.startSeconds = startSeconds;
        seconds = startSeconds;
        if(async) {
            taskID = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                runTasks();
                seconds--;
            },0,20).getTaskId();
        }else{
            taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                runTasks();
                seconds--;
            },0,20);
        }
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
