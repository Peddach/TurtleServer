package de.petropia.turtleServer.api.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    /**
     * Plays a sound to a player
     * @param player The player, that should hear the sound
     * @param sound The sound, the player should hear
     */
    public static void playSound(Player player, Sound sound){
        player.playSound(player.getLocation(), sound, 1, 1);
    }

    /**
     * Plays a sound to a player
     * @param player The player, that should hear the sound
     * @param sound The sound, the player should hear
     * @param volume The volume of the sound
     * @param pitch The pitch (speed) of the sound
     */
    public static void playSound(Player player, Sound sound, float volume, float pitch){
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

}
