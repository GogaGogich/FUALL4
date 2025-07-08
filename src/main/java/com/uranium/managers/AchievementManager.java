package com.uranium.managers;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AchievementManager {
    
    private final UraniumCraft plugin;
    private final Map<UUID, Set<Achievement>> playerAchievements = new ConcurrentHashMap<>();
    
    public AchievementManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    public void checkAchievements(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerStatsManager stats = plugin.getPlayerStatsManager();
        
        // Проверка всех достижений
        for (Achievement achievement : Achievement.values()) {
            if (!hasAchievement(player, achievement) && achievement.isUnlocked(stats.getPlayerStats(player))) {
                unlockAchievement(player, achievement);
            }
        }
    }
    
    public void unlockAchievement(Player player, Achievement achievement) {
        UUID playerId = player.getUniqueId();
        
        playerAchievements.computeIfAbsent(playerId, k -> new HashSet<>()).add(achievement);
        
        // Уведомление игрока
        MessageUtil.sendMessage(player, "&6&l[ДОСТИЖЕНИЕ] &a" + achievement.getDisplayName());
        MessageUtil.sendMessage(player, "&e" + achievement.getDescription());
        
        // Звуковой эффект
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        
        // Награда
        if (achievement.getReward() != null) {
            player.getInventory().addItem(achievement.getReward());
            MessageUtil.sendMessage(player, "&aВы получили награду: " + achievement.getReward().getItemMeta().getDisplayName());
        }
    }
    
    public boolean hasAchievement(Player player, Achievement achievement) {
        return playerAchievements.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(achievement);
    }
    
    public Set<Achievement> getPlayerAchievements(Player player) {
        return playerAchievements.getOrDefault(player.getUniqueId(), new HashSet<>());
    }
    
    public void saveAllData() {
        // Сохранение достижений всех игроков
    }
    
    public enum Achievement {
        FIRST_URANIUM("Первый уран", "Добыть первую урановую руду", null) {
            @Override
            public boolean isUnlocked(PlayerStatsManager.PlayerStats stats) {
                return stats.getUraniumMined() >= 1;
            }
        },
        
        URANIUM_MINER("Уран-майнер", "Добыть 100 урановой руды", null) {
            @Override
            public boolean isUnlocked(PlayerStatsManager.PlayerStats stats) {
                return stats.getUraniumMined() >= 100;
            }
        },
        
        RADIATION_SURVIVOR("Выживший в радиации", "Провести 10 минут в радиации", null) {
            @Override
            public boolean isUnlocked(PlayerStatsManager.PlayerStats stats) {
                return stats.getTimeInRadiation() >= 10 * 60 * 1000;
            }
        },
        
        FIRST_LABORATORY("Первая лаборатория", "Построить первую лабораторию", null) {
            @Override
            public boolean isUnlocked(PlayerStatsManager.PlayerStats stats) {
                return stats.getLaboratoriesBuilt() >= 1;
            }
        },
        
        RESEARCHER("Исследователь", "Завершить 10 исследований", null) {
            @Override
            public boolean isUnlocked(PlayerStatsManager.PlayerStats stats) {
                return stats.getResearchCompleted() >= 10;
            }
        },
        
        CENTRIFUGE_MASTER("Мастер центрифуги", "Использовать центрифугу 50 раз", null) {
            @Override
            public boolean isUnlocked(PlayerStatsManager.PlayerStats stats) {
                return stats.getCentrifugeUsed() >= 50;
            }
        },
        
        POWER_ARMOR_USER("Пользователь силовой брони", "Переключить режим силовой брони 100 раз", null) {
            @Override
            public boolean isUnlocked(PlayerStatsManager.PlayerStats stats) {
                return stats.getPowerArmorModeChanges() >= 100;
            }
        },
        
        RAILGUN_SHOOTER("Стрелок из рельсотрона", "Сделать 500 выстрелов из рельсотрона", null) {
            @Override
            public boolean isUnlocked(PlayerStatsManager.PlayerStats stats) {
                return stats.getRailgunShots() >= 500;
            }
        };
        
        private final String displayName;
        private final String description;
        private final org.bukkit.inventory.ItemStack reward;
        
        Achievement(String displayName, String description, org.bukkit.inventory.ItemStack reward) {
            this.displayName = displayName;
            this.description = description;
            this.reward = reward;
        }
        
        public abstract boolean isUnlocked(PlayerStatsManager.PlayerStats stats);
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public org.bukkit.inventory.ItemStack getReward() { return reward; }
    }
}
