package com.tchristofferson.unbreakables.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.tchristofferson.unbreakables.PlayerArmory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandAlias("armory")
public class ArmoryCommand extends BaseCommand {

    private final PlayerArmory armory;

    public ArmoryCommand(PlayerArmory armory) {
        this.armory = armory;
    }

    @Default
    public void armory(Player player) {
        Inventory inventory = armory.getArmory(player.getUniqueId());

        if (inventory != null)
            player.openInventory(inventory);
    }

}
