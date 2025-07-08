package com.uranium.managers;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {
    
    private final UraniumCraft plugin;
    private final Set<UUID> authorizedPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, String> pendingAuth = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> authAttempts = new ConcurrentHashMap<>();
    private final Map<UUID, Long> authCooldowns = new ConcurrentHashMap<>();
    
    // Секретные коды для авторизации (в реальном плагине должны быть в конфиге)
    private final Set<String> validCodes = Set.of(
        "URANIUM-2024",
        "NUCLEAR-TECH",
        "ATOM-POWER",
        "QUANTUM-ACCESS",
        "RADIATION-SAFE"
    );
    
    public AuthManager(UraniumCraft plugin) {
        this.plugin = plugin;
        startAuthCleanupTask();
    }
    
    public boolean isAuthorized(Player player) {
        return authorizedPlayers.contains(player.getUniqueId());
    }
    
    public boolean startAuth(Player player, String code) {
        UUID playerId = player.getUniqueId();
        
        // Проверка кулдауна
        if (authCooldowns.containsKey(playerId)) {
            long cooldownEnd = authCooldowns.get(playerId);
            if (System.currentTimeMillis() < cooldownEnd) {
                long remaining = (cooldownEnd - System.currentTimeMillis()) / 1000;
                MessageUtil.sendMessage(player, "&cПодождите " + remaining + " секунд перед следующей попыткой!");
                return false;
            }
        }
        
        // Проверка количества попыток
        int attempts = authAttempts.getOrDefault(playerId, 0);
        if (attempts >= 3) {
            authCooldowns.put(playerId, System.currentTimeMillis() + 300000); // 5 минут
            authAttempts.remove(playerId);
            MessageUtil.sendMessage(player, "&cСлишком много неудачных попыток! Кулдаун: 5 минут");
            return false;
        }
        
        if (validCodes.contains(code.toUpperCase())) {
            authorizedPlayers.add(playerId);
            authAttempts.remove(playerId);
            authCooldowns.remove(playerId);
            
            MessageUtil.sendMessage(player, "&a§l✓ АВТОРИЗАЦИЯ УСПЕШНА!");
            MessageUtil.sendMessage(player, "&eДобро пожаловать в мир атомных технологий!");
            MessageUtil.sendMessage(player, "&7Используйте /uranium для начала работы");
            
            // Визуальные эффекты успешной авторизации
            plugin.getVisualEffectsManager().playGUIOpenEffect(player, "achievements");
            plugin.getSoundManager().playPowerUpSound(player);
            
            return true;
        } else {
            attempts++;
            authAttempts.put(playerId, attempts);
            
            MessageUtil.sendMessage(player, "&c✗ Неверный код авторизации!");
            MessageUtil.sendMessage(player, "&7Попытка " + attempts + "/3");
            MessageUtil.sendMessage(player, "&8Обратитесь к администратору за кодом доступа");
            
            plugin.getSoundManager().playErrorSound(player);
            
            return false;
        }
    }
    
    public void revokeAuth(Player player) {
        authorizedPlayers.remove(player.getUniqueId());
        MessageUtil.sendMessage(player, "&cВаша авторизация отозвана!");
    }
    
    public void grantAuth(Player player) {
        authorizedPlayers.add(player.getUniqueId());
        MessageUtil.sendMessage(player, "&aВам предоставлен доступ к плагину!");
    }
    
    public boolean checkAuthAndNotify(Player player) {
        if (!isAuthorized(player)) {
            MessageUtil.sendMessage(player, "&c§l✗ ДОСТУП ЗАПРЕЩЕН!");
            MessageUtil.sendMessage(player, "&7Для использования плагина требуется авторизация");
            MessageUtil.sendMessage(player, "&eИспользуйте: &f/uraniumauth <код>");
            MessageUtil.sendMessage(player, "&8Обратитесь к администратору за кодом доступа");
            
            plugin.getSoundManager().playErrorSound(player);
            return false;
        }
        return true;
    }
    
    private void startAuthCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                
                // Очистка истекших кулдаунов
                authCooldowns.entrySet().removeIf(entry -> entry.getValue() < currentTime);
                
                // Сброс попыток авторизации каждый час
                if (currentTime % 3600000 < 60000) { // Каждый час
                    authAttempts.clear();
                }
            }
        }.runTaskTimer(plugin, 1200L, 1200L); // Каждую минуту
    }
    
    public Set<UUID> getAuthorizedPlayers() {
        return new HashSet<>(authorizedPlayers);
    }
    
    public void saveAllData() {
        // Сохранение авторизованных игроков
    }
    
    public void reload() {
        // Перезагрузка настроек авторизации
    }
}
