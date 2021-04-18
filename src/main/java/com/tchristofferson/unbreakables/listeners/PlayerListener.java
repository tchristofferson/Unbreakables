package com.tchristofferson.unbreakables.listeners;

import com.tchristofferson.unbreakables.Unbreakables;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener {

    private final Unbreakables plugin;

    public PlayerListener(Unbreakables plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        plugin.loadArmory(event.getPlayer());

        ItemStack itemStack = new ItemStack(Material.ELYTRA);
        ItemMeta itemMeta = itemStack.getItemMeta();
        ((Damageable) itemMeta).setDamage(Material.ELYTRA.getMaxDurability() - 1);
        itemStack.setItemMeta(itemMeta);

        event.getPlayer().getInventory().addItem(itemStack);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        plugin.saveArmory(event.getPlayer(), false);
    }
}
