package com.uranium.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {
    
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize("&7[&6UraniumCraft&7] " + message));
    }
    
    public static void sendMessage(Player player, String message) {
        player.sendMessage(colorize("&7[&6UraniumCraft&7] " + message));
    }
    
    public static void log(String message) {
        System.out.println(colorize("[UraniumCraft] " + message));
    }
    
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(colorize(message));
    }
    
    public static void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(colorize(title), colorize(subtitle), 20, 60, 20);
    }
    
    public static void broadcast(String message) {
        org.bukkit.Bukkit.broadcastMessage(colorize("&7[&6UraniumCraft&7] " + message));
    }
}
