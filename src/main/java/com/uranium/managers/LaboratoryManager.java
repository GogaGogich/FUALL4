package com.uranium.managers;

import com.uranium.UraniumCraft;
import com.uranium.gui.LaboratoryGUI;
import com.uranium.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LaboratoryManager {
    
    private final UraniumCraft plugin;
    private final Map<Location, LaboratoryData> laboratories = new ConcurrentHashMap<>();
    
    public LaboratoryManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    public boolean createLaboratory(Location location, Player player) {
        if (laboratories.containsKey(location)) {
            MessageUtil.sendMessage(player, "&cЛаборатория уже существует!");
            return false;
        }
        
        LaboratoryData data = new LaboratoryData(location, player.getUniqueId());
        laboratories.put(location, data);
        
        MessageUtil.sendMessage(player, "&aЛаборатория успешно создана!");
        plugin.getPlayerStatsManager().addLaboratoryBuilt(player);
        
        return true;
    }
    
    public void openLaboratoryGUI(Player player, Location location) {
        LaboratoryData data = laboratories.get(location);
        if (data == null) {
            MessageUtil.sendMessage(player, "&cЛаборатория не найдена!");
            return;
        }
        
        new LaboratoryGUI(plugin, player, data).open();
    }
    
    public boolean startResearch(Player player, Location location, ResearchType research) {
        LaboratoryData data = laboratories.get(location);
        if (data == null) {
            MessageUtil.sendMessage(player, "&cЛаборатория не найдена!");
            return false;
        }
        
        if (data.hasActiveResearch()) {
            MessageUtil.sendMessage(player, "&cЛаборатория уже проводит исследование!");
            return false;
        }
        
        if (!hasRequiredResources(player, research)) {
            MessageUtil.sendMessage(player, "&cНедостаточно ресурсов для исследования!");
            return false;
        }
        
        if (data.getEnergy() < research.getEnergyCost()) {
            MessageUtil.sendMessage(player, "&cНедостаточно энергии в лаборатории!");
            return false;
        }
        
        // Извлечение ресурсов
        extractResearchResources(player, research);
        
        // Запуск исследования
        data.startResearch(research);
        
        MessageUtil.sendMessage(player, "&aИсследование '" + research.getDisplayName() + "' начато!");
        MessageUtil.sendMessage(player, "&eВремя исследования: " + (research.getDuration() / 1000 / 60) + " минут");
        
        return true;
    }
    
    public void updateAllLaboratories() {
        for (Map.Entry<Location, LaboratoryData> entry : laboratories.entrySet()) {
            Location location = entry.getKey();
            LaboratoryData data = entry.getValue();
            
            updateLaboratory(location, data);
        }
    }
    
    private void updateLaboratory(Location location, LaboratoryData data) {
        // Обновление энергии
        data.updateEnergy();
        
        // Обновление активного исследования
        if (data.hasActiveResearch() && data.isResearchComplete()) {
            completeResearch(location, data);
        }
    }
    
    private void completeResearch(Location location, LaboratoryData data) {
        ResearchType research = data.getCurrentResearch();
        data.completeResearch();
        
        Player player = plugin.getServer().getPlayer(data.getOwnerId());
        if (player != null && player.isOnline()) {
            giveResearchResults(player, research);
            MessageUtil.sendMessage(player, "&aИсследование '" + research.getDisplayName() + "' завершено!");
            
            // Статистика
            plugin.getPlayerStatsManager().addResearchCompleted(player);
        }
    }
    
    private boolean hasRequiredResources(Player player, ResearchType research) {
        for (Map.Entry<Material, Integer> entry : research.getRequiredResources().entrySet()) {
            if (!player.getInventory().contains(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    private void extractResearchResources(Player player, ResearchType research) {
        for (Map.Entry<Material, Integer> entry : research.getRequiredResources().entrySet()) {
            ItemStack item = new ItemStack(entry.getKey(), entry.getValue());
            player.getInventory().removeItem(item);
        }
    }
    
    private void giveResearchResults(Player player, ResearchType research) {
        ItemStack result = getResearchResult(research);
        if (result != null) {
            player.getInventory().addItem(result);
            MessageUtil.sendMessage(player, "&a§l✓ Исследование завершено! Получен предмет: " + 
                result.getItemMeta().getDisplayName());
        }
    }
    
    private ItemStack getResearchResult(ResearchType research) {
        return switch (research) {
            case CHEM_PROTECTION_HELMET -> plugin.getUraniumItems().getChemProtectionHelmet();
            case CHEM_PROTECTION_CHESTPLATE -> plugin.getUraniumItems().getChemProtectionChestplate();
            case POWER_ARMOR_HELMET -> plugin.getUraniumItems().getPowerArmorHelmet();
            case POWER_ARMOR_CHESTPLATE -> plugin.getUraniumItems().getPowerArmorChestplate();
            case POWER_ARMOR_LEGGINGS -> plugin.getUraniumItems().getPowerArmorLeggings();
            case POWER_ARMOR_BOOTS -> plugin.getUraniumItems().getPowerArmorBoots();
            case RAILGUN -> plugin.getUraniumItems().getRailgun();
            case URANIUM_TABLET -> plugin.getUraniumItems().getUraniumTablet();
            case TELEPORT_CORE -> plugin.getUraniumItems().getTeleportCore();
            case CENTRIFUGE_CORE -> plugin.getUraniumItems().getCentrifugeCore();
            case QUANTUM_PROCESSOR -> new ItemStack(Material.REDSTONE_BLOCK); // Квантовый процессор
        };
    }
    
    public LaboratoryData getLaboratoryData(Location location) {
        return laboratories.get(location);
    }
    
    public boolean isLaboratory(Location location) {
        return laboratories.containsKey(location);
    }
    
    public void removeLaboratory(Location location) {
        laboratories.remove(location);
    }
    
    public void saveAllData() {
        // Сохранение данных всех лабораторий
    }
    
    public void reload() {
        // Перезагрузка настроек лабораторий
    }
    
    public static class LaboratoryData {
        private final Location location;
        private final UUID ownerId;
        private int level;
        private int energy;
        private int maxEnergy;
        private ResearchType currentResearch;
        private long researchStartTime;
        private Map<Material, Integer> storage;
        private List<ResearchType> completedResearch;
        
        public LaboratoryData(Location location, UUID ownerId) {
            this.location = location;
            this.ownerId = ownerId;
            this.level = 1;
            this.energy = 100;
            this.maxEnergy = 1000;
            this.storage = new HashMap<>();
            this.completedResearch = new ArrayList<>();
        }
        
        public void startResearch(ResearchType research) {
            this.currentResearch = research;
            this.researchStartTime = System.currentTimeMillis();
            this.energy -= research.getEnergyCost();
        }
        
        public void completeResearch() {
            if (currentResearch != null) {
                completedResearch.add(currentResearch);
                currentResearch = null;
            }
        }
        
        public boolean hasActiveResearch() {
            return currentResearch != null;
        }
        
        public boolean isResearchComplete() {
            return hasActiveResearch() && 
                   (System.currentTimeMillis() - researchStartTime) >= currentResearch.getDuration();
        }
        
        public void updateEnergy() {
            if (energy < maxEnergy) {
                energy += 10; // Регенерация энергии
                if (energy > maxEnergy) energy = maxEnergy;
            }
        }
        
        public void upgradeLaboratory() {
            if (level < 5) {
                level++;
                maxEnergy += 500;
            }
        }
        
        // Геттеры и сеттеры
        public Location getLocation() { return location; }
        public UUID getOwnerId() { return ownerId; }
        public int getLevel() { return level; }
        public int getEnergy() { return energy; }
        public int getMaxEnergy() { return maxEnergy; }
        public ResearchType getCurrentResearch() { return currentResearch; }
        public Map<Material, Integer> getStorage() { return storage; }
        public List<ResearchType> getCompletedResearch() { return completedResearch; }
        
        public void setEnergy(int energy) { this.energy = energy; }
        public void addEnergy(int amount) { this.energy = Math.min(maxEnergy, this.energy + amount); }
        public void removeEnergy(int amount) { this.energy = Math.max(0, this.energy - amount); }
        
        public long getResearchRemainingTime() {
            if (!hasActiveResearch()) return 0;
            return Math.max(0, currentResearch.getDuration() - (System.currentTimeMillis() - researchStartTime));
        }
        
        public int getResearchProgressPercentage() {
            if (!hasActiveResearch()) return 0;
            long elapsed = System.currentTimeMillis() - researchStartTime;
            return (int) ((elapsed * 100) / currentResearch.getDuration());
        }
    }
    
    public enum ResearchType {
        CHEM_PROTECTION_HELMET("Шлем химзащиты", 30000, 100, createResourceMap(Material.LEATHER, 5, Material.IRON_INGOT, 2)),
        CHEM_PROTECTION_CHESTPLATE("Костюм химзащиты", 45000, 150, createResourceMap(Material.LEATHER, 8, Material.IRON_INGOT, 3)),
        POWER_ARMOR_HELMET("Шлем силовой брони", 60000, 200, createResourceMap(Material.DIAMOND, 3, Material.REDSTONE, 10)),
        POWER_ARMOR_CHESTPLATE("Корпус силовой брони", 90000, 300, createResourceMap(Material.DIAMOND, 5, Material.REDSTONE, 15)),
        POWER_ARMOR_LEGGINGS("Поножи силовой брони", 75000, 250, createResourceMap(Material.DIAMOND, 4, Material.REDSTONE, 12)),
        POWER_ARMOR_BOOTS("Сапоги силовой брони", 60000, 200, createResourceMap(Material.DIAMOND, 3, Material.REDSTONE, 10)),
        RAILGUN("Рельсотрон", 120000, 400, createResourceMap(Material.IRON_INGOT, 10, Material.REDSTONE_BLOCK, 5)),
        URANIUM_TABLET("Урановый планшет", 90000, 300, createResourceMap(Material.GOLD_INGOT, 5, Material.REDSTONE_BLOCK, 3)),
        TELEPORT_CORE("Ядро телепорта", 150000, 500, createResourceMap(Material.ENDER_PEARL, 8, Material.DIAMOND, 4)),
        CENTRIFUGE_CORE("Ядро центрифуги", 120000, 400, createResourceMap(Material.IRON_BLOCK, 4, Material.REDSTONE_BLOCK, 2)),
        QUANTUM_PROCESSOR("Квантовый процессор", 180000, 600, createResourceMap(Material.DIAMOND_BLOCK, 2, Material.REDSTONE_BLOCK, 8));
        
        private final String displayName;
        private final long duration;
        private final int energyCost;
        private final Map<Material, Integer> requiredResources;
        
        ResearchType(String displayName, long duration, int energyCost, Map<Material, Integer> requiredResources) {
            this.displayName = displayName;
            this.duration = duration;
            this.energyCost = energyCost;
            this.requiredResources = requiredResources;
        }
        
        private static Map<Material, Integer> createResourceMap(Material material1, int amount1, Material material2, int amount2) {
            Map<Material, Integer> map = new HashMap<>();
            map.put(material1, amount1);
            map.put(material2, amount2);
            return map;
        }
        
        public String getDisplayName() { return displayName; }
        public long getDuration() { return duration; }
        public int getEnergyCost() { return energyCost; }
        public Map<Material, Integer> getRequiredResources() { return requiredResources; }
    }
}
