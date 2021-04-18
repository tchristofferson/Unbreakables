package com.tchristofferson.unbreakables;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerArmory {

    private final Map<UUID, Inventory> unbreakableItemsInventories = new HashMap<>();

    public synchronized Inventory getArmory(UUID uuid) {
        return unbreakableItemsInventories.get(uuid);
    }

    public synchronized void setArmory(UUID uuid, Inventory inventory) {
        unbreakableItemsInventories.put(uuid, inventory);
    }

    public synchronized Inventory removeArmory(UUID uuid) {
        return unbreakableItemsInventories.remove(uuid);
    }

}
