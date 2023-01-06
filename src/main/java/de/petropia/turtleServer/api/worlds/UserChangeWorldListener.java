package de.petropia.turtleServer.api.worlds;

import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserChangeWorldListener implements Listener {

    private static final HashMap<World, World> WORLD_LINK_MAP_NETHER = new HashMap<>();
    private static final HashMap<World, World> WORLD_LINK_MAP_END = new HashMap<>();

    @EventHandler
    public void onUserChangeWorld(EntityPortalReadyEvent event){
        if(event.getPortalType() == PortalType.NETHER) {
            if (WORLD_LINK_MAP_NETHER.containsKey(event.getEntity().getWorld())) {
                event.setTargetWorld(WORLD_LINK_MAP_NETHER.get(event.getEntity().getWorld()));
            }
        }
        if(event.getPortalType() == PortalType.ENDER){
            if (WORLD_LINK_MAP_END.containsKey(event.getEntity().getWorld())) {
                event.setTargetWorld(WORLD_LINK_MAP_END.get(event.getEntity().getWorld()));
            }
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event){
        removeFromHashmap(WORLD_LINK_MAP_END, event.getWorld());
        removeFromHashmap(WORLD_LINK_MAP_NETHER, event.getWorld());
    }

    private void removeFromHashmap(HashMap<World, World> map, World remove){
        Set<Map.Entry<World, World>> entries = new HashSet<>(map.entrySet());
        entries.forEach(entry -> {
            if(entry.getKey().equals(remove) || (entry.getValue().equals(remove))){
                map.remove(entry.getKey(), entry.getValue());
            }
        });
    }

    /**
     * Link two worlds together in both directions (world -> nether & nether -> world)
     * @param world Overworld
     * @param nether Netherworld
     */
    public static void linkWorldNether(World world, World nether){
        WORLD_LINK_MAP_NETHER.put(world, nether);
        WORLD_LINK_MAP_NETHER.put(nether, world);
    }

    /**
     * Link two worlds together in both directions (world -> end & end -> world)
     * @param world Overworld
     * @param end Endworld
     */
    public static void linkWorldEnd(World world, World end){
        WORLD_LINK_MAP_NETHER.put(world, end);
        WORLD_LINK_MAP_NETHER.put(end, world);
    }

    /**
     * Removes a link between two worlds
     * @param world Overworld
     * @param nether Netherworld
     */
    public static void removeLinkNether(World world, World nether){
        WORLD_LINK_MAP_NETHER.remove(world, nether);
        WORLD_LINK_MAP_NETHER.remove(nether, world);
    }

    /**
     * Removes a link between two worlds
     * @param world Overworld
     * @param end Endworld
     */
    public static void removeLinkEnd(World world, World end){
        WORLD_LINK_MAP_END.remove(world, end);
        WORLD_LINK_MAP_END.remove(end, world);
    }
}
