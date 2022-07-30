package de.petropia.turtleServer.server.user.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoDBHandler {
    private PetropiaPlayerDAO petropiaPlayerDAO;
    private final Hashtable<String, PetropiaPlayer> petropiaPlayerCache = new Hashtable<>();

    /**
     * A handler for the {@link PetropiaPlayer} to read and write to the database and cache
     */
    public MongoDBHandler() {
        FileConfiguration configuration = TurtleServer.getInstance().getConfig();   //loading credentails from config
        String hostname = configuration.getString("Mongo.Hostname");
        int port = configuration.getInt("Mongo.Port");
        String username = configuration.getString("Mongo.User");
        String database = configuration.getString("Mongo.Database");
        String password = configuration.getString("Mongo.Password");
        if(username == null || database == null || password == null || port == 0 || hostname == null){
            TurtleServer.getInstance().getLogger().warning("MongoDB Daten Fehler!");
            return;
        }

        ServerAddress serverAddress = new ServerAddress(hostname, port);    //Creating server address
        List<MongoCredential> credentials = new ArrayList<>();
        credentials.add(MongoCredential.createCredential(username, database, password.toCharArray()));
        MongoClient mongoClient = new MongoClient(serverAddress, credentials);

        Morphia morphia = new Morphia();
        morphia.map(PetropiaPlayer.class);

        Datastore datastore = morphia.createDatastore(mongoClient, database);
        datastore.ensureIndexes();

        petropiaPlayerDAO = new PetropiaPlayerDAO(PetropiaPlayer.class, datastore);
    }

    /**
     * Read a player from the cache or, if not present, the database.
     * @param uuid Mojang uuid of the player to read
     * @return A {@link CompletableFuture} which will be completed when the user is read from the database.
     * <b>The Future can be completed with null when the player is nether cached nor in the database. This means, he never joined the server</b>
     */
    public CompletableFuture<PetropiaPlayer> getPetropiaPlayerByUUID(String uuid){
        CompletableFuture<PetropiaPlayer> playerCompletableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getInstance(), () -> {
            if(petropiaPlayerCache.contains(uuid)){
               playerCompletableFuture.complete(petropiaPlayerCache.get(uuid));
               return;
            }
            PetropiaPlayer player = petropiaPlayerDAO.findOne("uuid", uuid);
            if(player != null){
                playerCompletableFuture.complete(player);
                cachePlayer(player);
                return;
            }
            playerCompletableFuture.complete(null);
        });
        return playerCompletableFuture;
    }

    /**
     * Read a player from the cache or, if not present, the database.
     * @param player Bukkit player to read
     * @return A {@link CompletableFuture} which will be completed when the user is read from the database.
     * <b>The Future can be completed with null when the player is nether cached nor in the database. This means, he never joined the server</b>
     */
    public CompletableFuture<PetropiaPlayer> getPetropiaPlayerByOnlinePlayer(Player player){
        return getPetropiaPlayerByUUID(player.getUniqueId().toString());
    }

    /**
     * Returns a list of all cached players. When a player joins the server, he is automatically cached.
     * @return List of cached players
     */
    public List<PetropiaPlayer> getChachedPlayers(){
        return new ArrayList<>(petropiaPlayerCache.values());
    }

    /**
     * Update a player on the db
     * @param player Player to update
     */
    public void savePlayer(PetropiaPlayer player){
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getInstance(), () -> {
            petropiaPlayerDAO.save(player);
        });
    }

    /**
     * Remove player from local cache. Sould only used on quit event
     * Internal use only
     * @param player Player to uncache
     */
    public void unCachePlayer(PetropiaPlayer player){
       if(petropiaPlayerCache.containsValue(player)){
           petropiaPlayerCache.remove(null, player);
       }
    }

    /**
     * Add a player to the player cache
     * <b>Internal use only</b>
     * @param player Player to cache
     */
    public void cachePlayer(PetropiaPlayer player){
        if(petropiaPlayerCache.containsValue(player)){
            return;
        }
        petropiaPlayerCache.put(player.getUuid(), player);
    }
}
