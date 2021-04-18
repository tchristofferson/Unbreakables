package com.tchristofferson.unbreakables;

import co.aikar.commands.PaperCommandManager;
import com.tchristofferson.unbreakables.commands.ArmoryCommand;
import com.tchristofferson.unbreakables.commands.CreateCommand;
import com.tchristofferson.unbreakables.commands.RepairCommand;
import com.tchristofferson.unbreakables.listeners.ItemListener;
import com.tchristofferson.unbreakables.listeners.PlayerListener;
import com.tchristofferson.unbreakables.messages.Messenger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Unbreakables extends JavaPlugin implements Listener {

    private File savesDir;
    private PlayerArmory armory;
    private Messenger messenger;
    private Economy economy;
    private Pricer pricer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        if (!setupEconomy()) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        savesDir = new File(getDataFolder(), "saves");

        if (!savesDir.mkdir())
            Bukkit.getLogger().severe("Failed to create saves directory!");

        this.pricer = new Pricer(this);
        this.messenger = new Messenger(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml")));
        this.armory = new PlayerArmory();
        Bukkit.getPluginManager().registerEvents(new ItemListener(this, armory), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(this, this);

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ArmoryCommand(armory));
        commandManager.registerCommand(new RepairCommand(this));
        commandManager.registerCommand(new CreateCommand(this));
    }

    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            saveArmory(onlinePlayer, true);
        }
    }

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent event) {
        if (!getConfig().getBoolean("disable-anvil-repairs"))
            return;

        ItemStack itemStack = event.getInventory().getItem(0);

        if (itemStack == null)
            return;

        if (UnbreakableUtil.isUnbreakable(this, itemStack))
            event.setResult(null);
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void saveArmory(Player player, boolean isDisabling) {
        UUID uuid = player.getUniqueId();
        Inventory inventory = armory.removeArmory(uuid);
        File file = new File(savesDir, uuid + ".yml");
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        fileConfiguration.set("name", player.getName());
        fileConfiguration.set("items", null);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);

            if (itemStack == null)
                continue;

            fileConfiguration.set("items." + i, itemStack);
        }

        if (isDisabling) {
            saveArmory(player, file, fileConfiguration);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> saveArmory(player, file, fileConfiguration));
        }
    }

    private synchronized void saveArmory(Player player, File file, FileConfiguration fileConfiguration) {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save " + player.getName() + "'s(" + player.getUniqueId() + ") armory!");
            e.printStackTrace();
        }
    }

    public synchronized void loadArmory(Player player) {
        File file = new File(savesDir, player.getUniqueId() + ".yml");
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        if (!fileConfiguration.contains("items")) {
            armory.setArmory(player.getUniqueId(),
                    Bukkit.createInventory(null, 36, ChatColor.BOLD + "" + ChatColor.GOLD + "Armory"));
            return;
        }

        ConfigurationSection section = fileConfiguration.getConfigurationSection("items");
        Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.BOLD + "" + ChatColor.GOLD + "Armory");

        for (String slotString : section.getKeys(false)) {
            int slot = Integer.parseInt(slotString);
            ItemStack itemStack = section.getItemStack(slotString);
            inventory.setItem(slot, itemStack);
        }

        armory.setArmory(player.getUniqueId(), inventory);
    }

    public Pricer getPricer() {
        return pricer;
    }

    public Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null)
            return false;

        economy = rsp.getProvider();
        return economy != null;
    }
}
