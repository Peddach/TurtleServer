package de.petropia.turtleServer.server.cloudNet;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.petropia.turtleServer.api.minigame.GameMode;
import de.petropia.turtleServer.api.minigame.GameState;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class CloudNetAdapter {

    private static final String ARENA_UPDATE_CHANNEL = "minigames_arena_updates";
    private static final String ARENA_UPDATE_MESSAGE = "ArenaUpdate";
    private static final String ARENA_DELETE_MESSAGE = "ArenaDelete";
    private static final String ARENA_UPDATE_RESEND_REQUEST = "ArenaUpdateResend";

    /**
     * @return Name of minecraft server instance (ex. Lobby-1)
     */
    public String getServerInstanceName() {
        return Wrapper.getInstance().getCurrentServiceInfoSnapshot().getName();
    }

    /**
     * @return Name of the task of the current service
     */
    public String getServerTaskName(){
        return Wrapper.getInstance().getServiceId().getTaskName();
    }

    /**
     * Send a player to the least full lobby
     * @param player Player to connect
     */
    public void sendPlayerToLobby(Player player) {
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        playerManager.getPlayerExecutor(Objects.requireNonNull(playerManager.getOnlinePlayer(player.getUniqueId()))).connectToFallback();
    }

    public void sendPlayerToServer(Player player, String server){
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        playerManager.getPlayerExecutor(Objects.requireNonNull(playerManager.getOnlinePlayer(player.getUniqueId()))).connect(server);
    }

    public void publishArenaUpdate(String game, String id, GameMode mode, int maxPlayers, GameState gameState, List<Player> players) {
        JsonDocument json = JsonDocument.newDocument();
        json.append("game", game);
        json.append("id", id);
        json.append("type", mode.name());
        json.append("playerCount", players.size());
        json.append("maxPlayerCount", maxPlayers);
        json.append("gameState", gameState.name());
        json.append("playerUUIDs", convertToUuidList(players));
        json.append("server", getServerInstanceName());
        ChannelMessage.builder()
                .channel(ARENA_UPDATE_CHANNEL)
                .message(ARENA_UPDATE_MESSAGE)
                .json(json)
                .targetAll()
                .build()
                .send();
    }

    public void publishArenaDelete(String id){
        JsonDocument json = JsonDocument.newDocument();
        json.append("server", getServerInstanceName());
        json.append("id", id);
        ChannelMessage.builder()
                .channel(ARENA_UPDATE_CHANNEL)
                .message(ARENA_DELETE_MESSAGE)
                .json(json)
                .targetAll()
                .build()
                .send();
    }

    public void publishArenaUpdateResendRequest(){
        ChannelMessage.builder()
                .channel(ARENA_UPDATE_CHANNEL)
                .message(ARENA_UPDATE_RESEND_REQUEST)
                .targetAll()
                .build()
                .send();
    }

    private String convertToUuidList(List<Player> playerList){
        StringBuilder players = new StringBuilder();
        for (Player player : playerList) players.append(" ").append(player.getUniqueId());
        return players.toString();
    }

    public static String getArenaUpdateChannelName(){
        return ARENA_UPDATE_CHANNEL;
    }

    public static String getArenaDeleteMessage() {
        return ARENA_DELETE_MESSAGE;
    }

    public static String getArenaUpdateMessage() {
        return ARENA_UPDATE_MESSAGE;
    }

    public static String getArenaUpdateResendRequestMessage() {
        return ARENA_UPDATE_RESEND_REQUEST;
    }
}
