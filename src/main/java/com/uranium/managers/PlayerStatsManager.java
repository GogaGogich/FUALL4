package com.uranium.managers;

import com.uranium.UraniumCraft;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatsManager {
    
    private final UraniumCraft plugin;
    private final ConcurrentHashMap<UUID, PlayerStats> playerStats = new ConcurrentHashMap<>();
    
    public PlayerStatsManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    public PlayerStats getPlayerStats(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStats());
    }
    
    // Методы для обновления статистики
    public void addUraniumMined(Player player) {
        getPlayerStats(player).addUraniumMined();
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void addRadiationPillTaken(Player player) {
        getPlayerStats(player).addRadiationPillTaken();
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void addTimeInRadiation(Player player, long time) {
        getPlayerStats(player).addTimeInRadiation(time);
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void addLaboratoryBuilt(Player player) {
        getPlayerStats(player).addLaboratoryBuilt();
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void addResearchCompleted(Player player) {
        getPlayerStats(player).addResearchCompleted();
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void addCentrifugeUsed(Player player) {
        getPlayerStats(player).addCentrifugeUsed();
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void addCentrifugeBuilt(Player player) {
        getPlayerStats(player).addCentrifugeBuilt();
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void addPowerArmorModeChange(Player player) {
        getPlayerStats(player).addPowerArmorModeChange();
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void addRailgunShot(Player player) {
        getPlayerStats(player).addRailgunShot();
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    public void saveAllData() {
        // Сохранение статистики всех игроков
    }
    
    public static class PlayerStats {
        private int uraniumMined = 0;
        private int radiationPillsTaken = 0;
        private long timeInRadiation = 0;
        private int laboratoriesBuilt = 0;
        private int researchCompleted = 0;
        private int centrifugeUsed = 0;
        private int centrifugeBuilt = 0;
        private int powerArmorModeChanges = 0;
        private int railgunShots = 0;
        
        // Методы для увеличения статистики
        public void addUraniumMined() { uraniumMined++; }
        public void addRadiationPillTaken() { radiationPillsTaken++; }
        public void addTimeInRadiation(long time) { timeInRadiation += time; }
        public void addLaboratoryBuilt() { laboratoriesBuilt++; }
        public void addResearchCompleted() { researchCompleted++; }
        public void addCentrifugeUsed() { centrifugeUsed++; }
        public void addCentrifugeBuilt() { centrifugeBuilt++; }
        public void addPowerArmorModeChange() { powerArmorModeChanges++; }
        public void addRailgunShot() { railgunShots++; }
        
        // Геттеры
        public int getUraniumMined() { return uraniumMined; }
        public int getRadiationPillsTaken() { return radiationPillsTaken; }
        public long getTimeInRadiation() { return timeInRadiation; }
        public int getLaboratoriesBuilt() { return laboratoriesBuilt; }
        public int getResearchCompleted() { return researchCompleted; }
        public int getCentrifugeUsed() { return centrifugeUsed; }
        public int getCentrifugeBuilt() { return centrifugeBuilt; }
        public int getPowerArmorModeChanges() { return powerArmorModeChanges; }
        public int getRailgunShots() { return railgunShots; }
    }
}
