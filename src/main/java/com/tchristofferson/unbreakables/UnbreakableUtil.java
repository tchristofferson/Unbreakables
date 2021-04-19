package com.tchristofferson.unbreakables;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnbreakableUtil {

    private static final String UNBREAKABLE_KEY = "unbreakable";
    private static final String UNUSABLE_KEY = "isUnusable";

    private static final String UNBREAKABLE_LORE_VALUE = ChatColor.GOLD + "" + ChatColor.BOLD + "Unbreakable";
    private static final String UNUSABLE_LORE_VALUE = ChatColor.RED + "" + ChatColor.BOLD + "Unusable";

    public static boolean isUnbreakableType(Material material) {
        String materialName = material.name();

        return ArmorType.isArmor(material)
                || materialName.endsWith("_SWORD")
                || materialName.endsWith("_PICKAXE")
                || materialName.endsWith("_AXE")
                || materialName.endsWith("_HOE")
                || materialName.endsWith("_SHOVEL");
    }

    public static boolean isUnbreakable(Plugin plugin, ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return false;

        return itemStack.getItemMeta().getPersistentDataContainer()
                    .getOrDefault(new NamespacedKey(plugin, UNBREAKABLE_KEY), PersistentDataType.BYTE, (byte) 0) == (byte) 1;
    }

    public static void makeUnbreakable(Plugin plugin, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = getItemLore(itemMeta);
        lore.add(UNBREAKABLE_LORE_VALUE);
        itemMeta.setLore(lore);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, UNBREAKABLE_KEY), PersistentDataType.BYTE, (byte) 1);

        itemStack.setItemMeta(itemMeta);
    }

    public static void makeBreakable(Plugin plugin, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = getItemLore(itemMeta);
        lore.removeAll(Arrays.asList(UNBREAKABLE_LORE_VALUE, UNUSABLE_LORE_VALUE));
        itemMeta.setLore(lore);
        itemMeta.getPersistentDataContainer().remove(new NamespacedKey(plugin, UNBREAKABLE_KEY));

        itemStack.setItemMeta(itemMeta);
    }

    public static boolean isUnusable(Plugin plugin, ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return false;

        return itemStack.getItemMeta().getPersistentDataContainer()
                .getOrDefault(new NamespacedKey(plugin, UNUSABLE_KEY), PersistentDataType.BYTE, (byte) 0) == (byte) 1;
    }

    public static void setUnusable(Plugin plugin, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        ((Damageable) itemMeta).setDamage(itemStack.getType().getMaxDurability() - 1);
        List<String> lore = getItemLore(itemMeta);
        lore.add(UNUSABLE_LORE_VALUE);
        itemMeta.setLore(lore);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, UNUSABLE_KEY), PersistentDataType.BYTE, (byte) 1);

        itemStack.setItemMeta(itemMeta);
    }

    public static void setUsable(Plugin plugin, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        ((Damageable) itemMeta).setDamage(0);
        List<String> lore = getItemLore(itemMeta);
        lore.remove(UNUSABLE_LORE_VALUE);
        itemMeta.setLore(lore);
        itemMeta.getPersistentDataContainer().remove(new NamespacedKey(plugin, UNUSABLE_KEY));

        itemStack.setItemMeta(itemMeta);
    }

    private static List<String> getItemLore(ItemMeta itemMeta) {
        return itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>(1);
    }

}
