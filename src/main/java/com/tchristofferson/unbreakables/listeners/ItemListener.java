package com.tchristofferson.unbreakables.listeners;

import com.tchristofferson.unbreakables.ArmorType;
import com.tchristofferson.unbreakables.PlayerArmory;
import com.tchristofferson.unbreakables.UnbreakableUtil;
import com.tchristofferson.unbreakables.Unbreakables;
import com.tchristofferson.unbreakables.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class ItemListener implements Listener {

    private final Unbreakables plugin;
    private final PlayerArmory armory;

    public ItemListener(Unbreakables plugin, PlayerArmory armory) {
        this.plugin = plugin;
        this.armory = armory;
    }

    @EventHandler
    public void onItemBreakEvent(PlayerItemBreakEvent event) {
        ItemStack itemStack = event.getBrokenItem();

        if (!UnbreakableUtil.isUnbreakable(plugin, itemStack))
            return;

        Player player = event.getPlayer();
        player.getInventory().remove(itemStack);
        UnbreakableUtil.setUnusable(plugin, itemStack);
        armory.getArmory(player.getUniqueId()).addItem(itemStack);
        plugin.getMessenger().sendMessage(player, Messages.ITEM_BROKEN);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (ArmorType.isArmor(itemStack.getType()))
            return;

        if (UnbreakableUtil.isUnusable(plugin, itemStack)) {
            event.setCancelled(true);
            plugin.getMessenger().sendMessage(player, Messages.ITEM_BROKEN);
        }
    }

    @EventHandler
    public void onEntityDamageEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity) {
            ItemStack itemStack = ((LivingEntity) event.getDamager()).getEquipment().getItemInMainHand();

            if (!ArmorType.isArmor(itemStack.getType()) && UnbreakableUtil.isUnbreakable(plugin, itemStack) && UnbreakableUtil.isUnusable(plugin, itemStack)) {
                event.setCancelled(true);

                if (event.getDamager().getType() == EntityType.PLAYER)
                    plugin.getMessenger().sendMessage(((Player) event.getDamager()), Messages.ITEM_BROKEN);
            }
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;

        if (event.getView().getBottomInventory().getType() != InventoryType.PLAYER)
            return;

        boolean isShift = event.isShiftClick();
        ItemStack itemStack = isShift ? event.getCurrentItem() : event.getCursor();

        if (itemStack == null || !ArmorType.isArmor(itemStack.getType())
                || !UnbreakableUtil.isUnbreakable(plugin, itemStack)
                || !UnbreakableUtil.isUnusable(plugin, itemStack))
            return;

        if (!isShift && event.getAction().name().startsWith("PLACE_") && event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
            return;
        }

        if (isShift && event.getSlotType() != InventoryType.SlotType.ARMOR) {
            ArmorType armorType = ArmorType.getArmorType(itemStack.getType());
            Inventory inventory = event.getView().getBottomInventory();
            //armorType isn't null because above it is checked to see if it is armor
            ItemStack currentArmor = ArmorType.getItemStack((PlayerInventory) inventory, armorType);

            if (currentArmor != null)
                return;

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        if (!(event.getView().getBottomInventory() instanceof PlayerInventory))
            return;

        ItemStack itemStack = event.getOldCursor();

        if (!ArmorType.isArmor(itemStack.getType())
                || !UnbreakableUtil.isUnbreakable(plugin, itemStack)
                || !UnbreakableUtil.isUnusable(plugin, itemStack))
            return;

        for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            int slot = entry.getKey();

            for (int i = 5; i <= 8; i++) {
                if (slot == i) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onDispenseArmorEvent(BlockDispenseArmorEvent event) {
        Bukkit.getLogger().info("Dispense Armor Event");
        ItemStack itemStack = event.getItem();

        if (UnbreakableUtil.isUnbreakable(plugin, itemStack) && UnbreakableUtil.isUnusable(plugin, itemStack))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!event.hasItem() || event.getItem() == null)
            return;

        ItemStack itemStack = event.getItem();

        if (!ArmorType.isArmor(itemStack.getType()) || !UnbreakableUtil.isUnbreakable(plugin, itemStack) ||
                !UnbreakableUtil.isUnusable(plugin, itemStack))
            return;

        event.setCancelled(true);
        plugin.getMessenger().sendMessage(event.getPlayer(), Messages.ITEM_BROKEN);
    }

}
