package de.petropia.turtleServer.server.user;

import de.petropia.turtleServer.server.TurtleServer;
import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Entity(value = "players")
public class PetropiaPlayer {

    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String uuid;
    @Indexed
    private String userName;
    private String skinTexture;
    private String skinTextureSignature;
    private boolean online;
    private String server;
    private int lastOnline;
    private boolean friendJoinMessage;
    @Property(value = "name_history")
    private List<String> nameHistory = new ArrayList<>();
    private HashMap<String, Double> stats;

    /**
     * A PetropiaPlayer is a player profile for the petropia network.
     * Every player who joined the network at least once is stored in a MongoDB Database.
     * The Petropia player is synchronized with the database when the player joins, so there
     * is no need to do it manually.
     */
    public PetropiaPlayer(){

    }

    /**
     * @return The database id of this {@link PetropiaPlayer} object
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * @return Mojang UUID of this player
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid of a player </br>
     * <b>This is internal use only. Changing the uuid can break the plugin</b>
     */
    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    /**
     * @return Last known username.
     * If the player was not online since he changes his name, this name won't be updated
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return The current base64 encoded texture of his skin
     * If the player was not online since he changes his name, this name won't be updated
     */
    public String getSkinTexture() {
        return skinTexture;
    }

    /**
     * Update players skin texture in base64 encoding
     * @param base64Texture Player texture
     */
    public void updateSkinTexture(String base64Texture){
        this.skinTexture = base64Texture;
    }

    /**
     * @return True when player is online on the network
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Update the players current online status on the network.
     * This is not meant for the api, internal use only
     * @param online false when offline
     */
    public void updateOnline(boolean online) {
        this.online = online;
    }

    /**
     * @return Current server the player is online. Null if not online
     */
    public String getServer() {
        if(server.equalsIgnoreCase("null")){
            return null;
        }
        return server;
    }

    /**
     * Update the current server from the player
     * This is not meant for the api, internal use only
     * @param server Current server
     */
    public void updateServer(String server) {
        this.server = server;
    }

    /**
     * @return Last logout time as Unix rime stamp
     */
    public int getLastOnline() {
        return lastOnline;
    }

    /**
     * Update the last logout time as unix timestamp
     * This is not meant for the api, internal use only
     * @param lastOnline lastLogout time as unix timestamp
     */
    public void updateLastOnline(int lastOnline) {
        this.lastOnline = lastOnline;
    }

    /**
     * @return List of all names a player had on this server
     */
    public List<String> getNameHistory() {
        return new ArrayList<>(nameHistory);
    }

    /**
     * Update the username. Remember to {@link PetropiaPlayer#updatePlayer()} to save changes
     * @param userName New username of player
     */
    public void updateUserName(String userName){
        nameHistory.add(userName);
        this.userName = userName;
    }

    /**
     * @return Signature of the players skin
     */
    public String getSkinTextureSignature() {
        return skinTextureSignature;
    }

    /**
     * Updated the skin
     * @param skinTextureSignature Signature of players skin
     */
    public void updateSkinTextureSignature(String skinTextureSignature) {
        this.skinTextureSignature = skinTextureSignature;
    }

    /**
     * Get the value of a stats property
     * @param identifier The unique identifier
     * @return the value
     */
    public double getStats(String identifier){
        return stats.get(identifier);
    }

    /**
     * Set a specific stats property
     * @param identifier Unique identifier
     * @param value The stats value
     */
    public void setStats(String identifier, double value){
        stats.put(identifier, value);
        updatePlayer();
    }

    /**
     * Increase the value of a stats property
     * @param identifier unique identifier
     * @param valueToIncrease the amout of which should be increased
     */
    public void increateStats(String identifier, double valueToIncrease){
        stats.put(identifier, stats.get(identifier) + valueToIncrease);
        updatePlayer();
    }

    /**
     * Internal use! Use the proper methods {@link PetropiaPlayer#increateStats(String, double)}, {@link PetropiaPlayer#setStats(String, double)} and {@link PetropiaPlayer#getStats(String)}
     * to modify the stats
     * @return HashMap with stats
     */
    public HashMap<String, Double> getStatsHashMap(){
        return stats;
    }

    /**
     * Set if a playersould recive a message when a friend connect to the server
     * @param sendMessage true if message sould be sended
     */
    public void updateFriendJoinMessage(boolean sendMessage){
        this.friendJoinMessage = sendMessage;
    }

    public boolean isFriendJoinMessageEnabled(){
        return this.friendJoinMessage;
    }

    /**
     * Updated the player on the Database
     */
    public CompletableFuture<PetropiaPlayer> updatePlayer(){
        return TurtleServer.getMongoDBHandler().savePlayer(this);
    }
}
