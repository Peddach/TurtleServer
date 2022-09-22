package de.petropia.turtleServer.server.stats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class StatsGuiListener implements Listener {

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event){
        if(StatsGui.getGuis().contains(event.getInventory())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerClose(InventoryCloseEvent event){
        StatsGui.getGuis().remove(event.getInventory());
    }
}
