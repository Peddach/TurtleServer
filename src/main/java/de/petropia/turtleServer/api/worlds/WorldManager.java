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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldManager {

    /**
     * Save a world to the worlds database
     * @param world The Bukkit world
     * @throws ZipException Fired when something went wrong while zipping the file
     * @return CompletableFuture with boolean: true = sueccessfully saved, false = error while saving
     */
    public static CompletableFuture<Boolean> saveToDBWorld(World world) throws ZipException {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Unload world " + world.getName());
        if(!Bukkit.getServer().unloadWorld(world, true)){
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
                if(!regionDir.exists() || !regionDir.isDirectory() || !regionDir.canRead()){
                    TurtleServer.getInstance().getMessageUtil().showDebugMessage("Error - Exists: " + regionDir.exists() + "| isDir: " + regionDir.isDirectory() + "| read: " +regionDir.canRead());
                    future.complete(false);
                    return;
                }
                zip.addFile(levelDat, parameters);
                zip.addFolder(entitiesDir, parameters);
                zip.addFolder(poiDir, parameters);
                zip.addFolder(dataDir, parameters);
                zip.addFolder(regionDir, parameters);
                zipFile = zip.getFile();
            } catch (IOException e) {
                e.printStackTrace();
                TurtleServer.getInstance().getLogger().warning("Exception: path: " + regionDir.getAbsolutePath());
                TurtleServer.getInstance().getLogger().warning("Exception: exists: " + regionDir.exists() + "| isDir: " + regionDir.isDirectory() + "| read: " +regionDir.canRead());
                future.complete(false);
                deleteZip(zipFile);
                return;
            }
            if(zipFile == null){
                future.complete(false);
                return;
            }
            try (FileInputStream stream = new FileInputStream(zipFile)){
                byte[] bytes = stream.readAllBytes();
                final File finalZipFile = zipFile;
                WorldDatabase.saveWorld(world.getName(), bytes, world.getEnvironment()).thenAccept(result -> {
                    if(result){
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
     * @param world The world to copy
     * @param newWorld Name of the new world
     * @return A {@link CompletableFuture} which gets completed when copied
     */
    public static @NotNull CompletableFuture<World> copyLocalWorld(World world, String newWorld){
        CompletableFuture<World> future = new CompletableFuture<>();
        if(world == null || newWorld.isEmpty() || newWorld.isBlank()){
            future.complete(null);
            throw new IllegalArgumentException("World is null or new world name is empty/blank");
        }
        World.Environment worldEnv = world.getEnvironment();
        String worldName = world.getName();
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
     * @param id The id in the database
     * @param loadedName The name the world sould get when loaded
     * @return A completeable future when the world is loaded
     */
    public static CompletableFuture<World> loadWorld(String id, String loadedName){
       final CompletableFuture<World> future = new CompletableFuture<>();
       WorldDatabase.loadWorldFromDB(id).thenAccept(record -> {
           try {
               File tmpDir = new File(TurtleServer.getInstance().getDataFolder(), "tempWorlds");
               TurtleServer.getInstance().getMessageUtil().showDebugMessage("Load world " + record.id() + " from DB");
               tmpDir.mkdirs();
               File outDir = new File(tmpDir, record.id() + ".zip");
               outDir.createNewFile();
               FileOutputStream fileOutputStream = new FileOutputStream(outDir);
               fileOutputStream.write(record.data());
               fileOutputStream.close();
               TurtleServer.getInstance().getMessageUtil().showDebugMessage("Unzip " + record.id());
               ZipFile outZip = new ZipFile(new File(tmpDir, record.id() + ".zip"));
               File finalWorldDir = new File(Bukkit.getWorldContainer(), loadedName);
               outZip.extractAll(finalWorldDir.getCanonicalPath());
               outZip.close();
               outZip.getFile().delete();
               TurtleServer.getInstance().getMessageUtil().showDebugMessage("Deleted " + record.id() + ".zip");
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
     * @param world Bukkit {@link World} to unload and delete async
     */
    public static void deleteLocalWorld(World world) {
        if(world == null){
            return;
        }
        if(world.getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
            TurtleServer.getInstance().getMessageUtil().showDebugMessage("Cant delete the default world");
            return;
        }
        world.getPlayers().forEach(player -> player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation()));
        final File worldDir = world.getWorldFolder();
        if(!Bukkit.unloadWorld(world, false)){
            TurtleServer.getInstance().getMessageUtil().showDebugMessage("Cant unload world: " + world.getName());
        }
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getInstance(), () -> deleteRecursive(worldDir));
    }

    public static void deleteDatabaseWorld(String id){
        WorldDatabase.deleteWorld(id.toLowerCase());
    }

    private static void deleteZip(@Nullable File zip){
        if (zip == null || !zip.exists()) {
            return;
        }
        zip.delete();
    }

    /**
     * Delete all content in a directory and the directory itself
     * @param directory The directory to delete
     */
    private static void deleteRecursive(File directory){
        if(directory == null || !directory.exists()){
            return;
        }
        File[] files = directory.listFiles();
        if(files == null){  //Can be null when dir is empty or IO error
            directory.delete();
            return;
        }
        for(File file : files) {
            if(file.isDirectory()) {
                deleteRecursive(file);  //delete subdir recursive
            } else {
                file.delete();
            }
        }
        directory.delete(); //delete root dir -> now it is empty
    }

    /**
     * Pregenerate Chunks from Spawn
     * @param world The world where the Chunks should generate
     * @param blocksFromSpawnToGen The Blocks in each direction to generate
     * @param lazy when lazy true, only if enough system resources are available, the chunks are goinig to be generated. This will take longer, but is better for the performance
     * @return A Furture which will be completed <b>synchronously</b> when all chunks are generated.
     */
    public static CompletableFuture<Boolean> generate(World world, int blocksFromSpawnToGen, boolean lazy){
        final CompletableFuture<Boolean> boolfuture = new CompletableFuture<>();
        new Thread(() -> {
            int blocksFromSpawn = blocksFromSpawnToGen;
            if(blocksFromSpawn % 16 != 0){
                blocksFromSpawn += blocksFromSpawn % 16;
            }
            int chunks = blocksFromSpawn >> 4;
            chunks = chunks * 2;    //double it, so we can subtract half of it later to generate in negative space -> 0-3 [double]-> 0-6 [subtract half]-> (-3)-3
            List<Location> chunksToGenerate = new ArrayList<>();
            for(int x = 0; x < chunks; x++){
                for (int y = 0; y < chunks; y++){
                    int xcoord = x - (chunks / 2);
                    int ycoord = y - (chunks / 2);
                    chunksToGenerate.add(new Location(world, xcoord * 16, 0, ycoord * 16));
                }
            }
            final int chunkBufferSize = TurtleServer.getInstance().getConfig().getInt("ChunkGenBuffer");
            List<CompletableFuture<Chunk>> buffer = new ArrayList<>();
            for(int i = 0; i < chunksToGenerate.size(); i++){
                if(lazy){
                    buffer.add(world.getChunkAtAsyncUrgently(chunksToGenerate.get(i), true));
                } else {
                    buffer.add(world.getChunkAtAsync(chunksToGenerate.get(i), true));
                }
                if(i % chunkBufferSize == 0 ||i == chunksToGenerate.size() - 1){
                    CompletableFuture.allOf(buffer.toArray(new CompletableFuture[0])).join();
                    buffer.clear();
                }
            }
            Bukkit.getScheduler().runTask(TurtleServer.getInstance(), () -> boolfuture.complete(true));
            chunksToGenerate.clear();
        }).start();
        return boolfuture;
    }
}
