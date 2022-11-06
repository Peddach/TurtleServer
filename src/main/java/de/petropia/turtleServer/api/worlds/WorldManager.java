package de.petropia.turtleServer.api.worlds;

import de.petropia.turtleServer.server.TurtleServer;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorldManager {

    public static void saveToDBWorld(World world) throws ZipException {
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Unload world " + world.getName());
        Bukkit.getServer().unloadWorld(world, true);
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Zip world" + world.getName());
        Bukkit.getScheduler().runTaskAsynchronously(TurtleServer.getInstance(), () -> {
            File worldDir = new File(Bukkit.getServer().getWorldContainer(), world.getName());
            File regionDir = new File(worldDir, "region");
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionLevel(CompressionLevel.MAXIMUM);
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            File zipFile;
            try (ZipFile zip = new ZipFile(world.getName())) {
                zip.addFolder(regionDir, parameters);
                zipFile = zip.getFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(zipFile == null){
                return;
            }
            try (FileInputStream stream = new FileInputStream(zipFile)){
                byte[] bytes = stream.readAllBytes();
                WorldDatabase.saveWorld(world.getName(), bytes, world.getEnvironment()).thenAccept(result -> {
                    if(result){
                        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Saved world " + world.getName() + " to DB");
                        return;
                    }
                    TurtleServer.getInstance().getMessageUtil().showDebugMessage("Error while trying to save " + world.getName() + " to DB!");
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
