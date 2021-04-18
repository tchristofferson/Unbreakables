package com.tchristofferson.unbreakables.messages;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Messenger {

    private final FileConfiguration messagesConfig;

    public Messenger(FileConfiguration messagesConfig) {
        this.messagesConfig = messagesConfig;
    }

    public void sendMessage(Player player, String key) {
        String message = getMessage(key);
        sendPlayerMessage(player, message);
    }

    public void sendMessage(Player player, String key, double price) {
        String message = getMessage(key).replace("%price%", String.valueOf(price));
        sendPlayerMessage(player, message);
    }

    private String getMessage(String key) {
        return messagesConfig.getString(key);
    }

    private void sendPlayerMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
