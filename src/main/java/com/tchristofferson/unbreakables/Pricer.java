package com.tchristofferson.unbreakables;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class Pricer {

    private final double repairCost;

    private final boolean useFunctionCost;
    private final double costPerEnchant;
    private final double costPerEnchantLevel;
    private final double netheriteCost;
    private final double elytraCost;
    private final double costPerDiamond;
    private final double costPerGold;
    private final double costPerIron;
    private final double costPerStone;
    private final double costPerWood;

    public Pricer(Unbreakables plugin) {

        FileConfiguration config = plugin.getConfig();
        this.repairCost = config.getDouble("repair-cost");
        this.useFunctionCost = config.getBoolean("use-function-cost");
        this.costPerEnchant = config.getDouble("function-cost.cost-per-enchant");
        this.costPerEnchantLevel = config.getDouble("function-cost.cost-per-enchant-level");
        this.netheriteCost = config.getDouble("function-cost.netherite-cost");
        this.elytraCost = config.getDouble("function-cost.elytra-cost");
        this.costPerDiamond = config.getDouble("function-cost.cost-per-diamond");
        this.costPerGold = config.getDouble("function-cost.cost-per-gold");
        this.costPerIron = config.getDouble("function-cost.cost-per-iron");
        this.costPerStone = config.getDouble("function-cost.cost-per-stone");
        this.costPerWood = config.getDouble("function-cost.cost-per-wood");
    }

    public double getPrice(ItemStack itemStack) {
        if (!useFunctionCost)
            return repairCost;

        double cost = 0;
        int materialCount = 1;
        Material material = itemStack.getType();
        String materialName = material.name();
        ArmorType armorType = ArmorType.getArmorType(itemStack.getType());

        if (armorType != null) {
            materialCount = armorType.numMaterials;
        } else {
            if (materialName.endsWith("_AXE") || materialName.endsWith("_PICKAXE"))
                materialCount = 3;
            else if (materialName.endsWith("_HOE") || materialName.endsWith("_SWORD"))
                materialCount = 2;
        }

        if (material == Material.ELYTRA)
            cost += elytraCost;
        else if (materialName.startsWith("NETHERITE_"))
            cost += (materialCount * costPerDiamond) + netheriteCost;
        else if (materialName.startsWith("DIAMOND_"))
            cost += materialCount * costPerDiamond;
        else if (materialName.startsWith("GOLDEN_"))
            cost += materialCount * costPerGold;
        else if (materialName.startsWith("IRON_"))
            cost += materialCount * costPerIron;
        else if (materialName.startsWith("STONE_"))
            cost += materialCount * costPerStone;
        else {
            cost += materialCount * costPerWood;
        }

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            Map<Enchantment, Integer> enchantments = itemMeta.getEnchants();
            cost += enchantments.size() * costPerEnchant;

            for (Integer level : enchantments.values()) {
                cost += level * costPerEnchantLevel;
            }
        }

        return cost;
    }
}
