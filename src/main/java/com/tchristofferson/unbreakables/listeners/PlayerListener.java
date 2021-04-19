package com.tchristofferson.unbreakables.listeners;

import com.tchristofferson.unbreakables.UnbreakableUtil;
import com.tchristofferson.unbreakables.Unbreakables;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
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
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        plugin.saveArmory(event.getPlayer(), false);
    }

    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if (event.getEntityType() != EntityType.PLAYER)
            return;

        Player player = (Player) event.getEntity();

        //Player is still gliding at this point when elytra break
        if (!player.isGliding())
            return;

        ItemStack itemStack = player.getInventory().getChestplate();

        if (itemStack == null || itemStack.getType() != Material.ELYTRA) {
            if (player.getItemOnCursor().getType() != Material.ELYTRA)
                return;
            else
                itemStack = player.getItemOnCursor();
        }

        if (!UnbreakableUtil.isUnbreakable(plugin, itemStack))
            return;

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (((Damageable) itemMeta).getDamage() >= Material.ELYTRA.getMaxDurability() - 2)
            UnbreakableUtil.setUnusable(plugin, itemStack);
    }
}
