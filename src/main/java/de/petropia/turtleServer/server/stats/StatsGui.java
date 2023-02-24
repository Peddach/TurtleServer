package de.petropia.turtleServer.server.stats;

import de.petropia.turtleServer.api.util.TimeUtil;
import de.petropia.turtleServer.server.user.PetropiaPlayer;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class StatsGui {
    private final PetropiaPlayer petropiaPlayer;
    public StatsGui(PetropiaPlayer petropiaPlayer, Player viewer) {
        this.petropiaPlayer = petropiaPlayer;
        Gui gui = Gui.gui()
                .title(Component.text("Stats", GOLD).decorate(TextDecoration.BOLD))
                .rows(3)
                .disableAllInteractions()
                .create();
        if(petropiaPlayer == null){
            gui.setItem(12, createNotFound());
            return;
        }
        gui.setItem(10, createMiscItem());
        gui.setItem(12, createChickenLeagueItem());
        gui.setItem(13, createBingoItem());
        gui.setItem(14, createSTNItem());
        gui.setItem(15, createSpacelifeItem());
        gui.open(viewer);
    }
    private GuiItem createNotFound(){
        return ItemBuilder.from(Material.BARRIER)
                .name(Component.text("Spieler konnte nicht gefunden werden!", RED))
                .asGuiItem();
    }
    private GuiItem createChickenLeagueItem() {
        Component name = Component.text("ChickenLeague").color(GOLD).decorate(TextDecoration.BOLD);
        Component points = Component.text("Punkte: ").color(GRAY).append(getStatsAsComponent("ChickenLeague_Points"));
        Component wins = Component.text("Gewonnene Runden: ").color(GRAY).append(getStatsAsComponent("ChickenLeague_Wins"));
        Component goals = Component.text("Tore: ").color(GRAY).append(getStatsAsComponent("ChickenLeague_Goals"));
        return ItemBuilder.from(Material.WOODEN_SHOVEL)
                .name(name)
                .lore(
                        Component.empty(),
                        points,
                        wins,
                        goals,
                        Component.empty())
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem();
    }

    private GuiItem createBingoItem() {
        Component name = Component.text("Bingo").color(GOLD).decorate(TextDecoration.BOLD);
        Component points = Component.text("Punkte: ").color(GRAY).append(getStatsAsComponent("Bingo_Points"));
        Component wins = Component.text("Gewonnene Runden: ").color(GRAY).append(getStatsAsComponent("Bingo_Wins"));
        Component quests = Component.text("Aufgaben: ").color(GRAY).append(getStatsAsComponent("Bingo_Quests"));
        return ItemBuilder.from(Material.MAP)
                .name(name)
                .lore(
                        Component.empty(),
                        points,
                        wins,
                        quests,
                        Component.empty())
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem();
    }

    private GuiItem createSTNItem() {
        Component name = Component.text("Survive The Night").color(GOLD).decorate(TextDecoration.BOLD);
        Component points = Component.text("Punkte: ").color(GRAY).append(getStatsAsComponent("DbD_Points"));
        Component wins = Component.text("Gewonnene Runden: ").color(GRAY).append(getStatsAsComponent("DbD_Wins"));
        Component kills = Component.text("Kills: ").color(GRAY).append(getStatsAsComponent("DbD_Kills"));
        Component glowstones = Component.text("Abgebaute Glowstones: ").color(GRAY).append(getStatsAsComponent("DbD_Glowstones"));
        return ItemBuilder.from(Material.CROSSBOW)
                .name(name)
                .lore(
                        Component.empty(),
                        points,
                        wins,
                        kills,
                        glowstones,
                        Component.empty())
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem();
    }

    private GuiItem createSpacelifeItem() {
        Component name = Component.text("Spacelife").color(GOLD).decorate(TextDecoration.BOLD);
        Component money = Component.text("Geld: ").color(GRAY).append(getStatsAsComponent("Survival_Money"));
        return ItemBuilder.from(Material.GOLDEN_APPLE)
                .name(name)
                .lore(
                        Component.empty(),
                        money,
                        Component.empty())
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem();
    }

    private GuiItem createMiscItem() {
        Component name = Component.text("Generelle Statistiken").color(GOLD).decorate(TextDecoration.BOLD);
        Component time = Component.text("Spielzeit: ").color(GRAY).append(getOnlineTimeAsComponent());
        return ItemBuilder.from(Material.CLOCK)
                .name(name)
                .lore(
                        Component.empty(),
                        time,
                        Component.empty())
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem();
    }

    private Component getStatsAsComponent(String key){
        double stats = petropiaPlayer.getStats(key);
        if(stats <= 0){
            return Component.text("Keine Daten").color(RED);
        }
        String value = Double.toString(stats);
        String[] splitString = value.split("\\.");
        String valueWithoutPoint = splitString[0];
        return Component.text(valueWithoutPoint).color(YELLOW);
    }

    private Component getOnlineTimeAsComponent(){
        int seconds = (int) petropiaPlayer.getStats("General_Playtime");
        return Component.text(TimeUtil.formatSeconds(seconds)).color(YELLOW);
    }
}
