package de.petropia.turtleServer.api.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtil {

    /**
     * @param material The material of the returned ItemStack
     * @param displayName The displayName of the returned ItemStack
     * @param enchanted if {@code true}: adds enchantment glow to the returned ItemStack
     * @param lore The content of the item-lore of the returned ItemStack
     * @return an ItemStack
     */
    public static ItemStack createItem(Material material, Component displayName, boolean enchanted, Component... lore){
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        meta.displayName(displayName);
        meta.lore(List.of(lore));

        if(enchanted){
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * @param material The material of the returned ItemStack
     * @param enchanted if {@code true}: adds enchantment glow to the returned ItemStack
     * @return an ItemStack
     */
    public static ItemStack createItem(Material material, boolean enchanted){
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        if(enchanted){
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);

        return item;
    }

}
