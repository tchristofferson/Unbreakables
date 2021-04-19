package com.tchristofferson.unbreakables.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.tchristofferson.unbreakables.Pricer;
import com.tchristofferson.unbreakables.UnbreakableUtil;
import com.tchristofferson.unbreakables.Unbreakables;
import com.tchristofferson.unbreakables.messages.Messages;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("unbreakables|unbreakable|unbreak|ub")
public class RepairCommand  extends BaseCommand {

    private final Unbreakables plugin;
    private final Pricer pricer;

    public RepairCommand(Unbreakables plugin) {
        this.plugin = plugin;
        this.pricer = plugin.getPricer();
    }

    @Subcommand("repair|rep")
    @CommandPermission("unbreakables.repair")
    public void repair(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (!UnbreakableUtil.isUnbreakable(plugin, itemStack)) {
            plugin.getMessenger().sendMessage(player, Messages.NOT_UNBREAKABLE);
            return;
        }

        if (!UnbreakableUtil.isUnusable(plugin, itemStack)) {
            plugin.getMessenger().sendMessage(player, Messages.ITEM_NOT_BROKEN);
            return;
        }

        double price = pricer.getPrice(itemStack);
        Economy economy = plugin.getEconomy();

        if (!economy.has(player, price)) {
            plugin.getMessenger().sendMessage(player, Messages.NOT_ENOUGH_MONEY);
            return;
        }

        EconomyResponse response = economy.withdrawPlayer(player, price);

        if (response.transactionSuccess()) {
            UnbreakableUtil.setUsable(plugin, itemStack);
            plugin.getMessenger().sendMessage(player, Messages.ITEM_REPAIRED, price);
        }
    }

}
