package de.petropia.turtleServer.server.commandBlocker;

import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandBlocker implements Listener {

    private static final HashMap<String, Component> blockedCommands = new HashMap<>();

    @EventHandler
    public void onPreCommand(PlayerCommandSendEvent event){
        if(event.getPlayer().hasPermission(" TurtleServer.bypass.commandblock")){
            return;
        }
        ArrayList<String> commands = new ArrayList<>(event.getCommands());
        for(int i = 0; i < event.getCommands().size(); i++){
            String cmd = commands.get(i);
            for(String blockedCmd : blockedCommands.keySet()){
                if(!cmd.equalsIgnoreCase(blockedCmd)){
                    continue;
                }
                event.getCommands().remove(cmd);
                commands.remove(cmd);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event){
        if(event.getPlayer().hasPermission(" TurtleServer.bypass.commandblock")){
            return;
        }
        blockedCommands.keySet().forEach(cmd -> {
            if(("/" + cmd).equalsIgnoreCase(event.getMessage())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(blockedCommands.get(cmd));
            }
        });
    }

    public static void loadCommandBlockList(){
        List<String> stringList = TurtleServer.getInstance().getConfig().getStringList("BlockedCommands");
        for(String str : stringList){
            String[] strings = str.split(";");
            if(strings.length != 2){
                TurtleServer.getInstance().getLogger().warning("Miss-formatted String in config for BlockedCommands: " + str);
                continue;
            }
            Component message = MiniMessage.miniMessage().deserialize(strings[1]);
            blockedCommands.put(strings[0], message);
            TurtleServer.getInstance().getComponentLogger().info(Component.text("BlockedCommand: " + strings[0]).append(message));
        }
    }

    public static HashMap<String, Component> getBlockedCommands(){
        return blockedCommands;
    }
}
