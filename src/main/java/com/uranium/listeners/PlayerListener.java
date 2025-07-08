package com.uranium.listeners;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final UraniumCraft plugin;
    
    public PlayerListener(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Инициализация данных игрока
        plugin.getRadiationManager().updatePlayerRadiation(event.getPlayer());
        
        // Проверка достижений
        plugin.getAchievementManager().checkAchievements(event.getPlayer());
        
        // Приветственное сообщение
        MessageUtil.sendMessage(event.getPlayer(), "&eДобро пожаловать в мир атомных технологий!");
        MessageUtil.sendMessage(event.getPlayer(), "&7Используйте &a/uranium guide &7для получения руководства");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Сохранение данных игрока при выходе
        // Данные сохраняются автоматически в менеджерах
    }
}
