package de.petropia.turtleServer.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

    /**
     * @param material The material of the returned ItemStack
     * @param displayName The displayName of the returned ItemStack
     * @param r The red value of the rgb values of the returned ItemStack's displayName
     * @param g The green value of the rgb values of the returned ItemStack's displayName
     * @param b The blue value of the rgb values of the returned ItemStack's displayName
     * @param enchanted if {@code true}: adds enchantment glow to the returned ItemStack
     * @param textDecorations All decorations, that should be applied to the returned ItemStack's displayName
     * @return an ItemStack
     */
    public static ItemStack createItem(Material material, String displayName, int r, int g, int b, boolean enchanted, TextDecoration... textDecorations){
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        Component displayNameComponent = Component.text(displayName).color(TextColor.color(r, g, b));
        for(TextDecoration decoration : textDecorations){
            displayNameComponent = displayNameComponent.decorate(decoration);
        }
        meta.displayName(displayNameComponent);

        if(enchanted){
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * @param material The material of the returned ItemStack
     * @param displayName The displayName of the returned ItemStack
     * @param enchanted if {@code true}: adds enchantment glow to the returned ItemStack
     * @param textDecorations All decorations, that should be applied to the returned ItemStack's displayName
     * @return an ItemStack
     */
    public static ItemStack createItem(Material material, String displayName, boolean enchanted, TextDecoration... textDecorations){
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        Component displayNameComponent = Component.text(displayName);
        for(TextDecoration decoration : textDecorations){
            displayNameComponent = displayNameComponent.decorate(decoration);
        }
        meta.displayName(displayNameComponent);

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
