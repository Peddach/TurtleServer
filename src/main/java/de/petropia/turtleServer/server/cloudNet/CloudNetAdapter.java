package de.petropia.turtleServer.server.cloudNet;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CloudNetAdapter {

    /**
     * @return Name of minecraft server instance (ex. Lobby-1)
     */
    public String getServerInstanceName() {
        return Wrapper.getInstance().getCurrentServiceInfoSnapshot().getName();
    }

    /**
     * Send a player to the least full lobby
     * @param player Player to connect
     */
    public void sendPlayerToLobby(Player player) {
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        playerManager.getPlayerExecutor(Objects.requireNonNull(playerManager.getOnlinePlayer(player.getUniqueId()))).connectToFallback();
    }
}
