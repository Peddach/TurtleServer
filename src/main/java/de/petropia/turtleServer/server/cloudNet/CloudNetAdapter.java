package de.petropia.turtleServer.server.cloudNet;

import de.petropia.turtleServer.api.minigame.GameMode;
import de.petropia.turtleServer.api.minigame.GameState;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.cloudNet.dto.ArenaDeleteDTO;
import de.petropia.turtleServer.server.cloudNet.dto.JoinGameMessageDTO;
import de.petropia.turtleServer.server.cloudNet.dto.JoinGameMessageResponseDTO;
import de.petropia.turtleServer.server.cloudNet.dto.PublishArenaUpdateDTO;
import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.wrapper.configuration.WrapperConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class CloudNetAdapter {

    private static final String ARENA_UPDATE_CHANNEL = "minigames_arena_updates";
    private static final String ARENA_UPDATE_MESSAGE = "ArenaUpdate";
    private static final String ARENA_DELETE_MESSAGE = "ArenaDelete";
    private static final String ARENA_UPDATE_RESEND_REQUEST = "ArenaUpdateResend";
    private static final String PLAYER_JOIN_GAME_QUERY_MESSAGE = "PlayerJoinGameQuery";
    private static final String PLAYER_JOIN_GAME_QUERY_CHANNEL = "minigames_join";
    private static BiFunction<String, UUID, Boolean> joinRequestResolver = (str, uuid) -> false;

    public CloudNetAdapter(){}

    /**
     * @return Name of minecraft server instance (ex. Lobby-1)
     */
    public String getServerInstanceName() {
        return wrapperConfigurationInstance().serviceInfoSnapshot().name();
    }

    /**
     * @return Name of the task of the current service
     */
    public String getServerTaskName(){
        return wrapperConfigurationInstance().serviceInfoSnapshot().serviceId().taskName();
    }

    /**
     * Send a player to the least full lobby
     * @param player Player to connect
     */
    public void sendPlayerToLobby(Player player) {
        playerManagerInstance().playerExecutor(player.getUniqueId()).connectToFallback();
    }

    public void sendPlayerToServer(Player player, String serverName){
        playerManagerInstance().playerExecutor(player.getUniqueId()).connect(serverName);
    }

    public void setJoinRequestResolver(BiFunction<String, UUID, Boolean> func){
        joinRequestResolver = func;
    }

    public BiFunction<String, UUID, Boolean> getJoinRequestResolver() {
        return joinRequestResolver;
    }

    /**
     * Add a player to a remote Arena
     *
     * @param player Player to add
     * @param gameID Game id
     * @param server Server of arena
     * @return CompletableFuture with boolean if successfull = true
     */
    public CompletableFuture<Boolean> joinPlayerGame(Player player, String gameID, String server){
        JoinGameMessageDTO joinGameMessage = new JoinGameMessageDTO(player.getUniqueId(), gameID);
        return CompletableFuture.supplyAsync(() -> {
            ChannelMessage response =  ChannelMessage.builder()
                    .targetService(server)
                    .channel(PLAYER_JOIN_GAME_QUERY_CHANNEL)
                    .message(PLAYER_JOIN_GAME_QUERY_MESSAGE)
                    .buffer(DataBuf.empty().writeObject(joinGameMessage))
                    .build()
                    .sendSingleQuery();
            if(response == null){
                TurtleServer.getInstance().getLogger().warning("Query to server " + server + " failed for joining Player " + player.getName() + " -> Response null");
                return false;
            }
            JoinGameMessageResponseDTO responseDTO = response.content().readObject(JoinGameMessageResponseDTO.class);
            response.content().close();
            boolean success = responseDTO.success();
            if(!success){
                return false;
            }
            Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> sendPlayerToServer(player, server));
            return true;
        });
    }

    public void publishArenaUpdate(String game, String id, GameMode mode, int maxPlayers, GameState gameState, List<Player> players) {
        PublishArenaUpdateDTO publishDTO = new PublishArenaUpdateDTO(
                game,
                id,
                mode,
                maxPlayers,
                players.size(),
                gameState,
                convertToUuidList(players),
                getServerInstanceName());
        ChannelMessage.builder()
                .channel(ARENA_UPDATE_CHANNEL)
                .message(ARENA_UPDATE_MESSAGE)
                .buffer(DataBuf.empty().writeObject(publishDTO))
                .targetAll()
                .build()
                .send();
    }

    public void publishArenaDelete(String id){
        ArenaDeleteDTO arenaDeleteDTO = new ArenaDeleteDTO(
                getServerInstanceName(),
                id
        );
        ChannelMessage.builder()
                .channel(ARENA_UPDATE_CHANNEL)
                .message(ARENA_DELETE_MESSAGE)
                .buffer(DataBuf.empty().writeObject(arenaDeleteDTO))
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
        for (Player player : playerList) {
            players.append(",").append(player.getUniqueId());
        }
        return players.toString();
    }

    public PlayerManager playerManagerInstance(){
        return InjectionLayer.ext().instance(ServiceRegistry.class).firstProvider(PlayerManager.class);
    }

    private WrapperConfiguration wrapperConfigurationInstance(){
        return InjectionLayer.ext().instance(WrapperConfiguration.class);
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
    public static String getPlayerJoinGameQueryMessage(){
        return PLAYER_JOIN_GAME_QUERY_MESSAGE;
    }

    public static String getPlayerJoinGameQueryChannel(){
        return PLAYER_JOIN_GAME_QUERY_CHANNEL;
    }
}
