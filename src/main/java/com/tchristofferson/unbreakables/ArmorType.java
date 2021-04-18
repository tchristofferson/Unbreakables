package com.tchristofferson.unbreakables;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public enum ArmorType {

    ELYTRA(0),
    HELMET(5),
    CHEST_PLATE(8),
    LEGGINGS(7),
    BOOTS(4);

    public final int numMaterials;

    ArmorType(int numMaterials) {
        this.numMaterials = numMaterials;
    }

    public static ItemStack getItemStack(PlayerInventory inventory, ArmorType armorType) {
        switch (armorType) {
            case HELMET:
                return inventory.getHelmet();
            case CHEST_PLATE:
                return inventory.getChestplate();
            case LEGGINGS:
                return inventory.getLeggings();
            case BOOTS:
                return inventory.getBoots();
        }

        return null;
    }

    public static ArmorType getArmorType(Material material) {
        if (material == Material.ELYTRA)
            return ArmorType.ELYTRA;

        String materialName = material.name();

        if (materialName.endsWith("_HELMET"))
            return ArmorType.HELMET;

        if (materialName.endsWith("_CHESTPLATE"))
            return ArmorType.CHEST_PLATE;

        if (materialName.endsWith("_LEGGINGS"))
            return ArmorType.LEGGINGS;

        if (materialName.endsWith("_BOOTS"))
            return ArmorType.BOOTS;

        return null;
    }

    public static boolean isArmor(Material material) {
        return getArmorType(material) != null;
    }
}
