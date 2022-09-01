package de.petropia.turtleServer.api.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
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
     * @param enchanted if {@code true}: adds enchantment glow to the potion
     * @param lore The content of the item-lore of the potion
     * @return A potion ItemStack
     */
    public static ItemStack createPotion(PotionType potionType, int amount, boolean splash, boolean enchanted, Component... lore){
        ItemStack potion;

        if(splash){
            potion = createItem(Material.SPLASH_POTION, amount, false, lore);
        }else{
            potion = createItem(Material.POTION, amount, false, lore);
        }

        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        meta.lore(List.of(lore));
        meta.setBasePotionData(new PotionData(potionType));

        if(enchanted){
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        potion.setItemMeta(meta);

        return potion;
    }

    /**
     * @param potionType The type of the potion
     * @param amount The amount of potions in the ItemStack
     * @param displayName The displayName of the potion
     * @param splash if {@code true}, creates a splash potion instead of a normal one
     * @param enchanted if {@code true}: adds enchantment glow to the potion
     * @param lore The content of the item-lore of the potion
     * @return A potion ItemStack
     */
    public static ItemStack createPotion(PotionType potionType, int amount, Component displayName, boolean splash, boolean enchanted, Component... lore){
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

        if(enchanted){
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        potion.setItemMeta(meta);

        return potion;
    }

    public ItemStack[] createLeatherArmor(Color color, boolean enchanted){
        ItemStack[] armor = new ItemStack[4];

        armor[0] = createItem(Material.LEATHER_HELMET, 1, enchanted);
        armor[1] = createItem(Material.LEATHER_CHESTPLATE, 1, enchanted);
        armor[2] = createItem(Material.LEATHER_LEGGINGS, 1, enchanted);
        armor[3] = createItem(Material.LEATHER_BOOTS, 1, enchanted);

        for(ItemStack itemStack : armor) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(color);
            itemStack.setItemMeta(meta);
        }

        return armor;
    }

    public ItemStack[] createLeatherArmor(Color color, int enchantmentLevel, Enchantment... enchantments){
        ItemStack[] armor = new ItemStack[4];

        armor[0] = createItem(Material.LEATHER_HELMET, 1, enchantments, enchantmentLevel);
        armor[1] = createItem(Material.LEATHER_CHESTPLATE, 1, enchantments, enchantmentLevel);
        armor[2] = createItem(Material.LEATHER_LEGGINGS, 1, enchantments, enchantmentLevel);
        armor[3] = createItem(Material.LEATHER_BOOTS, 1, enchantments, enchantmentLevel);

        for(ItemStack itemStack : armor) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(color);
            itemStack.setItemMeta(meta);
        }

        return armor;
    }
}
