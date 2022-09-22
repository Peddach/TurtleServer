package de.petropia.turtleServer.server.stats;

import de.petropia.turtleServer.server.TurtleServer;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatsGui {

    private final PetropiaPlayer petropiaPlayer;
    private final Inventory inventory = Bukkit.createInventory(null, 4*9, Component.text("Stats").color(NamedTextColor.GREEN));
    private static final List<Inventory> GUIS = new ArrayList<>();

    public StatsGui(PetropiaPlayer petropiaPlayer) {
        TurtleServer.getInstance().getMessageUtil().showDebugMessage("Stats gui open");
        this.petropiaPlayer = petropiaPlayer;
        fillInv();
        Player player = Bukkit.getPlayer(UUID.fromString(petropiaPlayer.getUuid()));
        player.openInventory(inventory);
        GUIS.add(inventory);
    }

    private void fillInv(){
        inventory.setItem(12, createChickenLeagueItem());
    }

    private ItemStack createChickenLeagueItem(){
        ItemStack item = new ItemStack(Material.GOLDEN_SHOVEL);
        item.editMeta(meta -> {
           meta.displayName(Component.text("ChickenLeague").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
           List<Component> lore = new ArrayList<>();
           double points = petropiaPlayer.getStats("ChickenLeague_Points");
           double goals = petropiaPlayer.getStats("ChickenLeague_Goals");
           double wins = petropiaPlayer.getStats("ChickenLeague_Wins");
           lore.add(Component.empty());
           lore.add(Component.text("Punkte: ").color(NamedTextColor.GRAY).append(Component.text(points).color(NamedTextColor.YELLOW)));
           lore.add(Component.text("Gewonnene Runden: ").color(NamedTextColor.GRAY).append(Component.text(wins).color(NamedTextColor.YELLOW)));
           lore.add(Component.text("Tore: ").color(NamedTextColor.GRAY).append(Component.text(goals).color(NamedTextColor.YELLOW)));
           lore.add(Component.empty());
           meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
           meta.lore(lore);
        });
        return item;
    }

    public static List<Inventory> getGuis(){
        return GUIS;
    }
}
