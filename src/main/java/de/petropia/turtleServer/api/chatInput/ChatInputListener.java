package de.petropia.turtleServer.api.chatInput;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class ChatInputListener implements Listener {

    private static final HashMap<Player, ChatInput> INPUT_HASH_MAP = new HashMap<>();

    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent event){
        if(!INPUT_HASH_MAP.containsKey(event.getPlayer())){
            return;
        }
        ChatInput chatInput = INPUT_HASH_MAP.get(event.getPlayer());
        event.setCancelled(true);
        if(!chatInput.input(PlainTextComponentSerializer.plainText().serialize(event.originalMessage()))){
            return;
        }
        unregisterChatinput(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        unregisterChatinput(event.getPlayer());
        INPUT_HASH_MAP.get(event.getPlayer()).input("Abbrechen");
    }

    /**
     * Register a Chat input
     * @param input The ChatInput
     */
    public static void registerChatInput(ChatInput input){
        if(INPUT_HASH_MAP.containsKey(input.getPlayer())){
            INPUT_HASH_MAP.remove(input.getPlayer()).input("Abbrechen");
        }
        INPUT_HASH_MAP.put(input.getPlayer(), input);
    }

    /**
     * Unregister a ChatInput
     * @param player The ChatInput
     */
    public static void unregisterChatinput(Player player){
        INPUT_HASH_MAP.remove(player);
    }
}
