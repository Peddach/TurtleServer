package de.petropia.turtleServer.server.user.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.experimental.filters.Filters;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoDBHandler {
    private final Hashtable<String, PetropiaPlayer> petropiaPlayerUUIDCache = new Hashtable<>();
    private final Hashtable<String, PetropiaPlayer> petropiaPlayerNameCache = new Hashtable<>();
    private Datastore datastore;

    /**
     * A handler for the {@link PetropiaPlayer} to read and write to the database and cache
     */
    public MongoDBHandler() {
        FileConfiguration configuration = TurtleServer.getPlugin().getConfig();   //loading credentails from config
        String hostname = configuration.getString("Mongo.Hostname");
        int port = configuration.getInt("Mongo.Port");
        String username = configuration.getString("Mongo.User");
        String database = configuration.getString("Mongo.Database");
        String password = configuration.getString("Mongo.Password");
        if (password == null) {
            TurtleServer.getPlugin().getLogger().warning("Missing password!");
            return;
        }
        if (username == null) {
            TurtleServer.getPlugin().getLogger().warning("Missing username!");
            return;
        }
        if (database == null) {
            TurtleServer.getPlugin().getLogger().warning("Missing database!");
            return;
        }
        if (hostname == null) {
            TurtleServer.getPlugin().getLogger().warning("Missing hostname!");
            return;
        }
        if (port == 0) {
            TurtleServer.getPlugin().getLogger().warning("Missing port!");
            return;
        }
        MongoClient mongoClient = MongoClients.create("mongodb://" + username + ":" + password + "@" + hostname + ":" + port + "/?authSource=" + database);
        datastore = Morphia.createDatastore(mongoClient, database);
        datastore.getMapper().map(PetropiaPlayer.class);
        datastore.ensureIndexes();
    }

    /**
     * Read a player from the cache or, if not present, the database.
     *
     * @param uuid Mojang uuid of the player to read
     * @return A {@link CompletableFuture} which will be completed when the user is read from the database.
     * <b>The Future can be completed with null when the player is nether cached nor in the database. This means, he never joined the server</b>
     */
    public CompletableFuture<PetropiaPlayer> getPetropiaPlayerByUUID(String uuid) {
        CompletableFuture<PetropiaPlayer> playerCompletableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getPlugin(), () -> {
            if (petropiaPlayerUUIDCache.contains(uuid)) {
                playerCompletableFuture.complete(petropiaPlayerUUIDCache.get(uuid));
                return;
            }
            PetropiaPlayer player = datastore.find(PetropiaPlayer.class).filter(Filters.eq("uuid", uuid)).first();
            if (player != null) {
                playerCompletableFuture.complete(player);
                return;
            }
            playerCompletableFuture.complete(null);
        });
        return playerCompletableFuture;
    }

    /**
     * Read a player from the cache or, if not present, the database.
     *
     * @param username Mojang username of player. Note, some players may share the same name, because the players name was not updated since the last join
     * @return A {@link CompletableFuture} which will be completed with the player. If two players have the same name, the one who joined last is picked
     * <b>The Future can be completed with null when the player is nether cached nor in the database. This means, he never joined the server or the name has a spelling mistake (case sensetive)</b>
     */
    public CompletableFuture<PetropiaPlayer> getPetropiaPlayerByUsername(String username) {
        CompletableFuture<PetropiaPlayer> playerCompletableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getPlugin(), () -> {
            if (petropiaPlayerNameCache.contains(username)) {
                playerCompletableFuture.complete(petropiaPlayerNameCache.get(username));
                return;
            }
            PetropiaPlayer player = null;
            List<PetropiaPlayer> playerQuery = datastore.find(PetropiaPlayer.class).filter(Filters.eq("userName", username)).stream().toList();
            if (playerQuery.size() == 1) {
                player = playerQuery.get(0);
            }
            if (playerQuery.size() > 1) {
                int lastJoin = 0;
                PetropiaPlayer currentLastJoin = null;
                for (PetropiaPlayer i : playerQuery) {
                    if (i.getLastOnline() > lastJoin) {
                        lastJoin = i.getLastOnline();
                        currentLastJoin = i;
                    }
                }
                player = currentLastJoin;
            }

            if (player != null) {
                playerCompletableFuture.complete(player);
                return;
            }
            playerCompletableFuture.complete(null);
        });
        return playerCompletableFuture;
    }

    /**
     * Read a player from the cache or, if not present, the database.
     *
     * @param player Bukkit player to read
     * @return A {@link CompletableFuture} which will be completed when the user is read from the database.
     * <b>The Future can be completed with null when the player is nether cached nor in the database. This means, he never joined the server</b>
     */
    public CompletableFuture<PetropiaPlayer> getPetropiaPlayerByOnlinePlayer(Player player) {
        return getPetropiaPlayerByUUID(player.getUniqueId().toString());
    }

    /**
     * Returns a list of all cached players. When a player joins the server, he is automatically cached.
     *
     * @return List of cached players
     */
    public List<PetropiaPlayer> getChachedPlayers() {
        return new ArrayList<>(petropiaPlayerUUIDCache.values());
    }

    /**
     * Update a player on the db
     *
     * @param player Player to update
     */
    public CompletableFuture<PetropiaPlayer> savePlayer(PetropiaPlayer player) {
        CompletableFuture<PetropiaPlayer> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getPlugin(), () -> {
            datastore.save(player);
            future.complete(player);
        });
        return future;
    }

    /**
     * Remove player from local cache. Sould only used on quit event
     * Internal use only
     *
     * @param player Player to uncache
     */
    public void unCachePlayer(PetropiaPlayer player) {
        petropiaPlayerUUIDCache.remove(player.getUuid());
        petropiaPlayerNameCache.remove(player.getUserName());
    }

    /**
     * Add a player to the player cache. Only players online on the current server should be cached
     * <b>Internal use only</b>
     *
     * @param player Player to cache
     */
    public void cachePlayer(PetropiaPlayer player) {
        if (!petropiaPlayerUUIDCache.containsValue(player)) {
            petropiaPlayerUUIDCache.put(player.getUuid(), player);
        }
        if (!petropiaPlayerNameCache.containsValue(player)) {
            petropiaPlayerNameCache.put(player.getUserName(), player);
        }
    }
}
