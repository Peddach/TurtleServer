package de.petropia.turtleServer.server.stats;

import de.petropia.turtleServer.api.util.ItemUtil;
import de.petropia.turtleServer.api.util.TimeUtil;
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

    private static final List<Inventory> GUIS = new ArrayList<>();
    private final PetropiaPlayer petropiaPlayer;
    private final Inventory inventory;

    public StatsGui(PetropiaPlayer petropiaPlayer) {

        //TODO Find out why it wont open... :-D

        Player player = Bukkit.getPlayer(UUID.fromString(petropiaPlayer.getUuid()));
        inventory = Bukkit.createInventory(player, 3 * 9, Component.text("Stats").color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
        this.petropiaPlayer = petropiaPlayer;
        Bukkit.getScheduler().runTaskLater(TurtleServer.getInstance(), () -> {
            fillInv();
            player.openInventory(inventory);
            GUIS.add(inventory);
        }, 1);
    }

    public static List<Inventory> getGuis() {
        return GUIS;
    }

    private void fillInv() {
        if (petropiaPlayer == null) {
            inventory.setItem(12, ItemUtil.createItem(Material.BARRIER, 1, Component.text("Kein Spieler gefunden").color(NamedTextColor.DARK_RED), false, false));
            return;
        }
        inventory.setItem(10, createGeneralItem());
        inventory.setItem(12, createChickenLeagueItem());
        inventory.setItem(13, createBingoItem());
        inventory.setItem(14, createMasterbuildersItem());
        inventory.setItem(15, createDbDItem());
        inventory.setItem(16, createSurvivalItem());
    }

    private ItemStack createChickenLeagueItem() {
        ItemStack item = new ItemStack(Material.WOODEN_SHOVEL);
        item.editMeta(meta -> {
            meta.displayName(Component.text("ChickenLeague").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Punkte: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("ChickenLeague_Points")));
            lore.add(Component.text("Gewonnene Runden: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("ChickenLeague_Wins")));
            lore.add(Component.text("Tore: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("ChickenLeague_Goals")));
            lore.add(Component.empty());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.lore(lore);
        });
        return item;
    }

    private ItemStack createBingoItem() {
        ItemStack item = new ItemStack(Material.MAP);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Bingo").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Punkte: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("Bingo_Points")));
            lore.add(Component.text("Aufgaben: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("Bingo_Quests")));
            lore.add(Component.text("Gewonnene Runden: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("Bingo_Wins")));
            lore.add(Component.empty());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.lore(lore);
        });
        return item;
    }

    private ItemStack createMasterbuildersItem() {
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Masterbuilders").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Punkte: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("Masterbuilders_points")));
            lore.add(Component.empty());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.lore(lore);
        });
        return item;
    }

    private ItemStack createDbDItem() {
        ItemStack item = new ItemStack(Material.CROSSBOW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Survive The Night").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Punkte: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("DbD_Points")));
            lore.add(Component.text("Gewonnene Runden: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("DbD_Wins")));
            lore.add(Component.text("Kills: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("DbD_Kills")));
            lore.add(Component.text("Abgebaute Glowstones: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("DbD_Glowstones")));
            lore.add(Component.empty());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.lore(lore);
        });
        return item;
    }

    private ItemStack createSurvivalItem() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Spacelife").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Geld: ").color(NamedTextColor.GRAY).append(getStatsAsComponent("Survival_Money")));
            lore.add(Component.empty());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.lore(lore);
        });
        return item;
    }

    private ItemStack createGeneralItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Generelle Statistiken").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Spielzeit: ").color(NamedTextColor.GRAY).append(getOnlineTimeAsComponent()));
            lore.add(Component.empty());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.lore(lore);
        });
        return item;
    }

    private Component getStatsAsComponent(String key){
        double stats = petropiaPlayer.getStats(key);
        if(stats <= 0){
            return Component.text("Keine Daten").color(NamedTextColor.RED);
        }
        String value = Double.toString(stats);
        String[] splitString = value.split("\\.");
        String valueWithoutPoint = splitString[0];
        return Component.text(valueWithoutPoint).color(NamedTextColor.YELLOW);
    }

    private Component getOnlineTimeAsComponent(){
        int seconds = (int) petropiaPlayer.getStats("General_Playtime");
        return Component.text(TimeUtil.formatSeconds(seconds)).color(NamedTextColor.YELLOW);
    }
}
