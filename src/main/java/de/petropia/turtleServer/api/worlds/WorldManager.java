package de.petropia.turtleServer.api.worlds;

import com.mongodb.lang.Nullable;
import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.util.TriState;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
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
        }
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Zip world" + world.getName());
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getInstance(), () -> {
            File worldDir = new File(Bukkit.getServer().getWorldContainer(), world.getName());
            File regionDir = new File(worldDir, "region");
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
                zip.addFile(regionDir, parameters);
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
               if(!tmpDir.exists()){
                   tmpDir.mkdirs();
               }
               TurtleServer.getInstance().getMessageUtil().showDebugMessage("Load world " + record.id() + " from DB");
               File outDir = new File(tmpDir, record.id() + ".zip");
               outDir.mkdirs();
               FileOutputStream fileOutputStream = new FileOutputStream(outDir);
               fileOutputStream.write(record.data());
               fileOutputStream.close();
               TurtleServer.getInstance().getMessageUtil().showDebugMessage("Unzip " + record.id());
               ZipFile outZip = new ZipFile(outDir);
               outZip.extractAll(new File(Bukkit.getServer().getWorldContainer(), record.id()).getCanonicalPath());
               outZip.close();
               outDir.delete();
               TurtleServer.getInstance().getMessageUtil().showDebugMessage("Deleted" + record.id() + ".zip");
               WorldCreator creator = new WorldCreator(loadedName);
               creator.environment(record.environment());
               creator.keepSpawnLoaded(TriState.FALSE);
               Bukkit.getScheduler().runTask(TurtleServer.getInstance(), creator::createWorld);
               TurtleServer.getInstance().getMessageUtil().showDebugMessage("Loaded " + record.id() + " successfully!");
           } catch (IOException e) {
               TurtleServer.getInstance().getMessageUtil().showDebugMessage("Error while loading " + record.id() + " : " + e.getMessage());
               e.printStackTrace();
               throw new RuntimeException(e);
           }
       });
       return future;
    }

    private static void deleteZip(@Nullable File zip){
        if (zip == null || !zip.exists()) {
            return;
        }
        zip.delete();
    }
}
