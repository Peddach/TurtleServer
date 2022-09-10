package de.petropia.turtleServer.server.user.database.listener;

import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerShutdownListener implements Listener {

    private static boolean serverLocked = false;

    @EventHandler
    public void onServerShutdownCommand(ServerCommandEvent event){
        if(!(event.getCommand().equalsIgnoreCase("stop") || event.getCommand().equalsIgnoreCase("/stop"))){
            return;
        }
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Shutdown hook triggered");
        event.setCancelled(true);
        TurtleServer.getInstance().shutdownServer();
    }

    @EventHandler
    public void onServerShutdownCommandByPlayer(PlayerCommandPreprocessEvent event){
        if(!event.getPlayer().hasPermission(" TurtleServer.bypass.commandblock")){
            return;
        }
        if(!(event.getMessage().equalsIgnoreCase("stop") || event.getMessage().equalsIgnoreCase("/stop"))){
            return;
        }
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Shutdown hook triggered");
        event.setCancelled(true);
        TurtleServer.getInstance().shutdownServer();
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event){
        if(serverLocked){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Der Server mit dem du dich verbinden willst stopt grade! Versuche es später erneut"));
        }
    }

    public static void lockServer(){
        serverLocked = true;
    }

}
