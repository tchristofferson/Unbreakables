package com.tchristofferson.unbreakables.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.tchristofferson.unbreakables.UnbreakableUtil;
import com.tchristofferson.unbreakables.Unbreakables;
import com.tchristofferson.unbreakables.messages.Messages;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("unbreakables|unbreakable|unbreak|ub")
public class CreateCommand extends BaseCommand {

    private final Unbreakables plugin;

    public CreateCommand(Unbreakables plugin) {
        this.plugin = plugin;
    }

    @Subcommand("create")
    public void create(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (UnbreakableUtil.isUnbreakable(plugin, itemStack)) {
            plugin.getMessenger().sendMessage(player, Messages.ALREADY_UNBREAKABLE);
            return;
        }

        if (!UnbreakableUtil.isUnbreakableType(itemStack.getType())) {
            plugin.getMessenger().sendMessage(player, Messages.CANT_CREATE);
            return;
        }

        double price = plugin.getConfig().getDouble("initial-cost");

        if (!plugin.getEconomy().has(player, price)) {
            plugin.getMessenger().sendMessage(player, Messages.NOT_ENOUGH_MONEY);
            return;
        }

        EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, price);

        if (response.transactionSuccess()) {
            UnbreakableUtil.makeUnbreakable(plugin, itemStack);
            plugin.getMessenger().sendMessage(player, Messages.ITEM_CREATED, price);
        }
    }

}
