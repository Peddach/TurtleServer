package de.petropia.turtleServer.api.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.List;

public class ItemUtil {

    /**
     * @param material The material of the returned ItemStack
     * @param amount The amount of items in the ItemStack
     * @param displayName The displayName of the returned ItemStack
     * @param enchanted if {@code true}: adds enchantment glow to the returned ItemStack
     * @param lore The content of the item-lore of the returned ItemStack
     * @return an ItemStack
     */
    public static ItemStack createItem(Material material, int amount, Component displayName, boolean enchanted, Component... lore){
        ItemStack item = new ItemStack(material, amount);

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
     * @param amount The amount of items in the ItemStack
     * @param enchanted if {@code true}: adds enchantment glow to the returned ItemStack
     * @param lore The content of the item-lore of the returned ItemStack
     * @return an ItemStack
     */
    public static ItemStack createItem(Material material, int amount, boolean enchanted, Component... lore){
        ItemStack item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
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
     * @param amount The amount of items in the ItemStack
     * @param enchantments The enchantments, the item should have
     * @param lore The content of the item-lore of the returned ItemStack
     * @return an ItemStack
     */
    public static ItemStack createItem(Material material, int amount, Enchantment[] enchantments, int enchantmentLevel, Component... lore){
        ItemStack item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
        meta.lore(List.of(lore));

        for(Enchantment enchantment : enchantments){
            meta.addEnchant(enchantment, enchantmentLevel, true);
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * @param material The material of the returned ItemStack
     * @param amount The amount of items in the ItemStack
     * @param displayName The displayName of the returned ItemStack
     * @param enchantments The enchantments, the item should have
     * @param lore The content of the item-lore of the returned ItemStack
     * @return an ItemStack
     */
    public static ItemStack createItem(Material material, int amount, Component displayName, Enchantment[] enchantments, int enchantmentLevel, Component... lore){
        ItemStack item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayName);
        meta.lore(List.of(lore));

        for(Enchantment enchantment : enchantments){
            meta.addEnchant(enchantment, enchantmentLevel, true);
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * @param potionType The type of the potion
     * @param amount The amount of potions in the ItemStack
     * @param splash if {@code true}, creates a splash potion instead of a normal one
     * @param lore The content of the item-lore of the potion
     * @return A potion ItemStack
     */
    public static ItemStack createPotion(PotionType potionType, int amount, boolean splash, Component... lore){
        ItemStack potion;

        if(splash){
            potion = createItem(Material.SPLASH_POTION, amount, false, lore);
        }else{
            potion = createItem(Material.POTION, amount, false, lore);
        }

        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        meta.lore(List.of(lore));
        meta.setBasePotionData(new PotionData(potionType));

        potion.setItemMeta(meta);

        return potion;
    }

    /**
     * @param potionType The type of the potion
     * @param amount The amount of potions in the ItemStack
     * @param splash if {@code true}, creates a splash potion instead of a normal one
     * @param displayName The displayName of the potion
     * @param lore The content of the item-lore of the potion
     * @return A potion ItemStack
     */
    public static ItemStack createPotion(PotionType potionType, int amount, boolean splash, Component displayName, Component... lore){
        ItemStack potion;

        if(splash){
            potion = createItem(Material.SPLASH_POTION, amount, false, lore);
        }else{
            potion = createItem(Material.POTION, amount, false, lore);
        }

        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        meta.displayName(displayName);
        meta.lore(List.of(lore));
        meta.setBasePotionData(new PotionData(potionType));

        potion.setItemMeta(meta);

        return potion;
    }

}
