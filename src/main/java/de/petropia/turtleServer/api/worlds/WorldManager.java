package de.petropia.turtleServer.api.worlds;

import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.util.TriState;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WorldManager {

    /**
     * Load the spawn world, if configured in the config.yml
     */
    public static void loadSpawnWorld() {
        List<String> configList = TurtleServer.getInstance().getConfig().getStringList("DefaultWorldOverride");
        HashMap<String, String> taskWorldIDMap = new HashMap<>();
        for (String str : configList) {
            String[] strings = str.split(":");
            if (strings.length != 2) {
                TurtleServer.getInstance().getLogger().warning("Wrong format in Config for DefaultWorldOverride: " + str);
                continue;
            }
            taskWorldIDMap.put(strings[0], strings[1]);
        }
        String task = TurtleServer.getInstance().getCloudNetAdapter().getServerTaskName();
        if (!taskWorldIDMap.containsKey(task)) {
            return;
        }
        String levelName;
        try (InputStream inputStream = new FileInputStream(new File(Bukkit.getPluginsFolder().getParentFile(), "server.properties"))) {
            Properties properties = new Properties();
            properties.load(inputStream);
            levelName = properties.getProperty("level-name");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        TurtleServer.getInstance().getLogger().info("Loading map for " + task + " with ID: " + taskWorldIDMap.get(task) + "! Level-Name=" + levelName);
        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (file.getName().equalsIgnoreCase(levelName)) {
                TurtleServer.getInstance().getLogger().warning("Found a default level: " + levelName + "! Deleting world directory " + levelName);
                deleteRecursive(file);
                break;
            }
        }
        loadWorld(taskWorldIDMap.get(task), levelName, false).join();
    }

    /**
     * Save a world to the worlds database
     *
     * @param world The Bukkit world
     * @return CompletableFuture with boolean: true = sueccessfully saved, false = error while saving
     * @throws ZipException Fired when something went wrong while zipping the file
     */
    public static CompletableFuture<Boolean> saveToDBWorld(World world) throws ZipException {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Unload world " + world.getName());
        if (!Bukkit.getServer().unloadWorld(world, true)) {
            TurtleServer.getInstance().getMessageUtil().showDebugMessage("Failed to save world: " + world.getName());
            future.complete(false);
            return future;
        }
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Zip world" + world.getName());
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getInstance(), () -> {
            File worldDir = new File(Bukkit.getServer().getWorldContainer(), world.getName());
            File regionDir = new File(worldDir, "region");
            File entitiesDir = new File(worldDir, "entities");
            File levelDat = new File(worldDir, "level.dat");
            File poiDir = new File(worldDir, "poi");
            File dataDir = new File(worldDir, "data");
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionLevel(CompressionLevel.MAXIMUM);
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            File zipFile = null;
            try (ZipFile zip = new ZipFile(world.getName().toLowerCase() + ".zip")) {
                if (!regionDir.exists() || !regionDir.isDirectory() || !regionDir.canRead()) {
                    TurtleServer.getInstance().getMessageUtil().showDebugMessage("Error - Exists: " + regionDir.exists() + "| isDir: " + regionDir.isDirectory() + "| read: " + regionDir.canRead());
                    future.complete(false);
                    return;
                }
                if(levelDat.exists()) zip.addFile(levelDat, parameters);
                if(entitiesDir.exists()) zip.addFolder(entitiesDir, parameters);
                if(poiDir.exists()) zip.addFolder(poiDir, parameters);
                if(dataDir.exists()) zip.addFolder(dataDir, parameters);
                if(regionDir.exists()) zip.addFolder(regionDir, parameters);
                zipFile = zip.getFile();
            } catch (IOException e) {
                e.printStackTrace();
                TurtleServer.getInstance().getLogger().warning("Exception: path: " + regionDir.getAbsolutePath());
                TurtleServer.getInstance().getLogger().warning("Exception: exists: " + regionDir.exists() + "| isDir: " + regionDir.isDirectory() + "| read: " + regionDir.canRead());
                future.complete(false);
                deleteZip(zipFile);
                return;
            }
            if (zipFile == null) {
                future.complete(false);
                return;
            }
            try (FileInputStream stream = new FileInputStream(zipFile)) {
                byte[] bytes = stream.readAllBytes();
                final File finalZipFile = zipFile;
                WorldDatabase.saveWorld(world.getName(), bytes, world.getEnvironment()).thenAccept(result -> {
                    if (result) {
                        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Saved world " + world.getName() + " to DB");
                        future.complete(true);
                        deleteZip(finalZipFile);
                        return;
                    }
                    TurtleServer.getInstance().getMessageUtil().showDebugMessage("Error while trying to save " + world.getName() + " to DB!");
                    future.complete(false);
                    deleteZip(finalZipFile);
                });
            } catch (IOException e) {
                future.complete(false);
                deleteZip(zipFile);
                throw new RuntimeException(e);
            }
        });
        return future;
    }

    /**
     * This Method copies a local world
     *
     * @param world    The world to copy
     * @param newWorld Name of the new world
     * @return A {@link CompletableFuture} which gets completed when copied
     */
    public static @NotNull CompletableFuture<World> copyLocalWorld(World world, String newWorld) {
        CompletableFuture<World> future = new CompletableFuture<>();
        if (world == null || newWorld.isEmpty() || newWorld.isBlank()) {
            future.complete(null);
            throw new IllegalArgumentException("World is null or new world name is empty/blank");
        }
        World.Environment worldEnv = world.getEnvironment();
        String worldName = world.getName();
        TurtleServer.getInstance().getLogger().info("Copy world " + worldName + " to " + newWorld);
        Bukkit.unloadWorld(world, true);
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getInstance(), () -> {
            File regionDir = new File(world.getWorldFolder(), "region");
            File entitiesDir = new File(world.getWorldFolder(), "entities");
            File levelDat = new File(world.getWorldFolder(), "level.dat");
            File poiDir = new File(world.getWorldFolder(), "poi");
            File dataDir = new File(world.getWorldFolder(), "data");
            File newWorldDir = new File(Bukkit.getWorldContainer(), newWorld);
            newWorldDir.mkdirs();
            try {
                FileUtils.copyDirectory(regionDir, new File(newWorldDir, "region"));
                FileUtils.copyDirectory(entitiesDir, new File(newWorldDir, "entities"));
                FileUtils.copyDirectory(poiDir, new File(newWorldDir, "poi"));
                FileUtils.copyDirectory(dataDir, new File(newWorldDir, "data"));
                FileUtils.copyFileToDirectory(levelDat, newWorldDir);
            } catch (IOException e) {
                future.complete(null);
                throw new RuntimeException(e);
            }
            Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> {
                WorldCreator creator = new WorldCreator(worldName);
                creator.environment(worldEnv);
                creator.keepSpawnLoaded(TriState.FALSE);
                creator.createWorld();
            });
            Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> {
                WorldCreator creator = new WorldCreator(newWorld);
                creator.keepSpawnLoaded(TriState.FALSE);
                creator.environment(worldEnv);
                future.complete(creator.createWorld());
            });
        });
        return future;
    }

    /**
     * Load a world from the database
     *
     * @param id         The id in the database
     * @param loadedName The name the world sould get when loaded
     * @return A completeable future when the world is loaded
     */
    public static CompletableFuture<World> loadWorld(String id, String loadedName) {
        return loadWorld(id, loadedName, true);
    }

    /**
     * private implementation of method which allows not loading the world into Bukkit.
     *
     * @param id         id of world in DB
     * @param loadedName name when loaded
     * @param load       should Bukkit load the world
     * @return CompletableFuture -> completed when loaded/copied. <b>May be null when load is set to false!</b>
     */
    private static CompletableFuture<World> loadWorld(String id, String loadedName, boolean load) {
        final CompletableFuture<World> future = new CompletableFuture<>();
        WorldDatabase.loadWorldFromDB(id).thenAccept(record -> {
            try {
                String zipName = generateRandomString() + "_" + record.id();
                File tmpDir = new File(TurtleServer.getInstance().getDataFolder(), "tempWorlds");
                TurtleServer.getInstance().getMessageUtil().showDebugMessage("Load world " + record.id() + " from DB to " + zipName);
                tmpDir.mkdirs();
                File outDir = new File(tmpDir, zipName + ".zip");
                outDir.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(outDir);
                fileOutputStream.write(record.data());
                fileOutputStream.close();
                TurtleServer.getInstance().getMessageUtil().showDebugMessage("Unzip " + zipName);
                ZipFile outZip = new ZipFile(new File(tmpDir, zipName + ".zip"));
                File finalWorldDir = new File(Bukkit.getWorldContainer(), loadedName);
                outZip.extractAll(finalWorldDir.getCanonicalPath());
                outZip.close();
                outZip.getFile().delete();
                TurtleServer.getInstance().getMessageUtil().showDebugMessage("Deleted " + zipName + ".zip");
                File playerdata = new File(finalWorldDir, "playerdata");    //recreate playerdata directory, because IOException from MC-Server when not exists
                playerdata.mkdirs();
                if (!load) {
                    future.complete(null);
                    return;
                }
                WorldCreator creator = new WorldCreator(loadedName);
                creator.environment(record.environment());
                creator.keepSpawnLoaded(TriState.FALSE);
                Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> future.complete(creator.createWorld()));
                TurtleServer.getInstance().getMessageUtil().showDebugMessage("Loaded " + record.id() + " successfully!");
            } catch (IOException e) {
                TurtleServer.getInstance().getMessageUtil().showDebugMessage("Error while loading " + record.id() + " : " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        return future;
    }

    /**
     * Delete a world async on the local server and unloads it
     *
     * @param world Bukkit {@link World} to unload and delete async
     */
    public static void deleteLocalWorld(World world) {
        if (world == null) {
            return;
        }
        if (world.getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) {
            TurtleServer.getInstance().getMessageUtil().showDebugMessage("Cant delete the default world");
            return;
        }
        world.getPlayers().forEach(player -> player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation()));
        final File worldDir = world.getWorldFolder();
        if (!Bukkit.unloadWorld(world, false)) {
            TurtleServer.getInstance().getMessageUtil().showDebugMessage("Cant unload world: " + world.getName());
            TurtleServer.getInstance().getMessageUtil().showDebugMessage("Forcing to delete world: " + world.getName());
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(TurtleServer.getInstance(), () -> {
            TurtleServer.getInstance().getLogger().info("Deleting world " + world.getName());
            deleteRecursive(worldDir);
        }, 3*20);    //Add 3 sec delay, cause Bukkit unloading is weird
    }

    public static void deleteDatabaseWorld(String id) {
        WorldDatabase.deleteWorld(id.toLowerCase());
    }

    private static void deleteZip(@Nullable File zip) {
        if (zip == null || !zip.exists()) {
            return;
        }
        zip.delete();
    }

    /**
     * Delete all content in a directory and the directory itself
     *
     * @param directory The directory to delete
     */
    private static void deleteRecursive(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {  //Can be null when dir is empty or IO error
            directory.delete();
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteRecursive(file);  //delete subdir recursive
            } else {
                file.delete();
            }
        }
        directory.delete(); //delete root dir -> now it is empty
    }

    /**
     * Pregenerate Chunks from Spawn
     *
     * @param world                The world where the Chunks should generate
     * @param blocksFromSpawnToGen The Blocks in each direction to generate
     * @param lazy                 when lazy true, only if enough system resources are available, the chunks are goinig to be generated. This will take longer, but is better for the performance
     * @return A Furture which will be completed <b>synchronously</b> when all chunks are generated.
     */
    public static CompletableFuture<Boolean> generate(World world, int blocksFromSpawnToGen, boolean lazy) {
        final CompletableFuture<Boolean> boolfuture = new CompletableFuture<>();
        new Thread(() -> {
            int blocksFromSpawn = blocksFromSpawnToGen;
            if (blocksFromSpawn % 16 != 0) {
                blocksFromSpawn += blocksFromSpawn % 16;
            }
            int chunks = blocksFromSpawn >> 4;
            chunks = chunks * 2;    //double it, so we can subtract half of it later to generate in negative space -> 0-3 [double]-> 0-6 [subtract half]-> (-3)-3
            List<Location> chunksToGenerate = new ArrayList<>();
            for (int x = 0; x < chunks; x++) {
                for (int y = 0; y < chunks; y++) {
                    int xcoord = x - (chunks / 2);
                    int ycoord = y - (chunks / 2);
                    chunksToGenerate.add(new Location(world, xcoord * 16, 0, ycoord * 16));
                }
            }
            final int chunkBufferSize = TurtleServer.getInstance().getConfig().getInt("ChunkGenBuffer");
            List<CompletableFuture<Chunk>> buffer = new ArrayList<>();
            for (int i = 0; i < chunksToGenerate.size(); i++) {
                if (lazy) {
                    buffer.add(world.getChunkAtAsyncUrgently(chunksToGenerate.get(i), true));
                } else {
                    buffer.add(world.getChunkAtAsync(chunksToGenerate.get(i), true));
                }
                if (i % chunkBufferSize == 0 || i == chunksToGenerate.size() - 1) {
                    CompletableFuture.allOf(buffer.toArray(new CompletableFuture[0])).join();
                    buffer.clear();
                }
            }
            Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> boolfuture.complete(true));
            chunksToGenerate.clear();
        }).start();
        return boolfuture;
    }

    /**
     * Links two dimensions together
     * @param overworld Overworld
     * @param endOrNether Other Dimension
     */
    public static void linkWorlds(World overworld, World endOrNether){
        if(endOrNether.getEnvironment() == World.Environment.NETHER){
            UserChangeWorldListener.linkWorldNether(overworld, endOrNether);
        }
        if(endOrNether.getEnvironment() == World.Environment.THE_END) {
            UserChangeWorldListener.linkWorldEnd(overworld, endOrNether);
        }
        TurtleServer.getInstance().getLogger().info("Linked worlds " + overworld.getName() + " and " + endOrNether.getName());
    }

    /**
     * Unlinks two dimensions
     * @param overworld Overworld
     * @param endOrNether Other Dimension
     */
    public static void unlinkWorlds(World overworld, World endOrNether){
        if(endOrNether.getEnvironment() == World.Environment.NETHER){
            UserChangeWorldListener.removeLinkNether(overworld, endOrNether);
        }
        if(endOrNether.getEnvironment() == World.Environment.THE_END) {
            UserChangeWorldListener.removeLinkEnd(overworld, endOrNether);
        }
    }

    private static String generateRandomString(){
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
    }
}
