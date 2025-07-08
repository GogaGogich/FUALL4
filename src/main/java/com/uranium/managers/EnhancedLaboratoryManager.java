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

public class EnhancedLaboratoryManager extends LaboratoryManager {
    
    private final UraniumCraft plugin;
    private final Map<Location, EnhancedLaboratoryData> laboratories = new ConcurrentHashMap<>();
    
    public EnhancedLaboratoryManager(UraniumCraft plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    @Override
    public boolean createLaboratory(Location location, Player player) {
        if (laboratories.containsKey(location)) {
            MessageUtil.sendMessage(player, "&cЛаборатория уже существует!");
            return false;
        }
        
        EnhancedLaboratoryData data = new EnhancedLaboratoryData(location, player.getUniqueId());
        laboratories.put(location, data);
        
        MessageUtil.sendMessage(player, "&aЛаборатория успешно создана!");
        MessageUtil.sendMessage(player, "&7Начните с исследования квантового процессора!");
        plugin.getPlayerStatsManager().addLaboratoryBuilt(player);
        
        return true;
    }
    
    public static class EnhancedLaboratoryData extends LaboratoryData {
        
        public EnhancedLaboratoryData(Location location, UUID ownerId) {
            super(location, ownerId);
        }
    }
    
    public enum EnhancedResearchType {
        // Базовые исследования (уровень 1)
        QUANTUM_PROCESSOR("Квантовый процессор", 300000, 200, 
            createResourceMap(Material.DIAMOND, 4, Material.REDSTONE_BLOCK, 8, Material.GOLD_BLOCK, 2),
            "§5Основа для всех продвинутых технологий"),
        
        // Защитное снаряжение (уровень 1-2)
        CHEM_PROTECTION_UPGRADE("Улучшенная химзащита", 180000, 150,
            createResourceMap(Material.IRON_INGOT, 8, Material.LEATHER, 12),
            "§aУлучшает базовую химзащиту до 35% защиты"),
        
        // Силовая броня (уровень 2-3)
        POWER_ARMOR_HELMET("Шлем силовой брони", 600000, 400,
            createResourceMap(Material.DIAMOND, 5, Material.REDSTONE_BLOCK, 3, Material.GOLD_INGOT, 8),
            "§bПродвинутая защита с энергетическими модулями"),
        
        POWER_ARMOR_CHESTPLATE("Корпус силовой брони", 900000, 600,
            createResourceMap(Material.DIAMOND, 8, Material.REDSTONE_BLOCK, 5, Material.GOLD_INGOT, 12),
            "§bОсновная часть силовой брони"),
        
        POWER_ARMOR_LEGGINGS("Поножи силовой брони", 750000, 500,
            createResourceMap(Material.DIAMOND, 7, Material.REDSTONE_BLOCK, 4, Material.GOLD_INGOT, 10),
            "§bЗащита ног с системой усиления"),
        
        POWER_ARMOR_BOOTS("Сапоги силовой брони", 600000, 400,
            createResourceMap(Material.DIAMOND, 5, Material.REDSTONE_BLOCK, 3, Material.GOLD_INGOT, 8),
            "§bСапоги с системой прыжков"),
        
        // Высокотехнологичные устройства (уровень 3-4)
        RAILGUN("Рельсотрон", 1200000, 800,
            createResourceMap(Material.IRON_BLOCK, 6, Material.REDSTONE_BLOCK, 8, Material.DIAMOND, 4),
            "§cМощное энергетическое оружие"),
        
        URANIUM_TABLET("Урановый планшет", 900000, 600,
            createResourceMap(Material.GOLD_BLOCK, 3, Material.REDSTONE_BLOCK, 6, Material.DIAMOND, 3),
            "§6Универсальное устройство управления"),
        
        // Строительные компоненты (уровень 4-5)
        TELEPORT_TECHNOLOGY("Технология телепортации", 1500000, 1000,
            createResourceMap(Material.ENDER_PEARL, 16, Material.DIAMOND_BLOCK, 2, Material.REDSTONE_BLOCK, 10),
            "§dОткрывает возможность создания телепортов"),
        
        CENTRIFUGE_TECHNOLOGY("Технология центрифуг", 1200000, 800,
            createResourceMap(Material.IRON_BLOCK, 8, Material.REDSTONE_BLOCK, 6, Material.DIAMOND, 6),
            "§6Позволяет строить центрифуги"),
        
        // Продвинутые исследования (уровень 5)
        RADIATION_IMMUNITY("Иммунитет к радиации", 2400000, 1500,
            createResourceMap(Material.DIAMOND_BLOCK, 4, Material.GOLD_BLOCK, 8, Material.EMERALD_BLOCK, 2),
            "§aПолная защита от радиации"),
        
        ENERGY_EFFICIENCY("Энергоэффективность", 1800000, 1200,
            createResourceMap(Material.REDSTONE_BLOCK, 12, Material.DIAMOND, 8, Material.GOLD_BLOCK, 4),
            "§eУвеличивает эффективность всех устройств"),
        
        QUANTUM_ENHANCEMENT("Квантовое усиление", 3000000, 2000,
            createResourceMap(Material.NETHER_STAR, 1, Material.DIAMOND_BLOCK, 8, Material.EMERALD_BLOCK, 4),
            "§5Максимальное улучшение всех технологий");
        
        private final String displayName;
        private final long duration;
        private final int energyCost;
        private final Map<Material, Integer> requiredResources;
        private final String description;
        private final int requiredLevel;
        
        EnhancedResearchType(String displayName, long duration, int energyCost, 
                           Map<Material, Integer> requiredResources, String description) {
            this.displayName = displayName;
            this.duration = duration;
            this.energyCost = energyCost;
            this.requiredResources = requiredResources;
            this.description = description;
            this.requiredLevel = calculateRequiredLevel();
        }
        
        private int calculateRequiredLevel() {
            // Определяем требуемый уровень лаборатории на основе сложности
            if (energyCost <= 200) return 1;
            if (energyCost <= 500) return 2;
            if (energyCost <= 800) return 3;
            if (energyCost <= 1200) return 4;
            return 5;
        }
        
        private static Map<Material, Integer> createResourceMap(Material mat1, int amount1, 
                                                              Material mat2, int amount2) {
            Map<Material, Integer> map = new HashMap<>();
            map.put(mat1, amount1);
            map.put(mat2, amount2);
            return map;
        }
        
        private static Map<Material, Integer> createResourceMap(Material mat1, int amount1, 
                                                              Material mat2, int amount2,
                                                              Material mat3, int amount3) {
            Map<Material, Integer> map = new HashMap<>();
            map.put(mat1, amount1);
            map.put(mat2, amount2);
            map.put(mat3, amount3);
            return map;
        }
        
        // Геттеры
        public String getDisplayName() { return displayName; }
        public long getDuration() { return duration; }
        public int getEnergyCost() { return energyCost; }
        public Map<Material, Integer> getRequiredResources() { return requiredResources; }
        public String getDescription() { return description; }
        public int getRequiredLevel() { return requiredLevel; }
        
        public String getFormattedDuration() {
            long minutes = duration / 60000;
            if (minutes >= 60) {
                long hours = minutes / 60;
                long remainingMinutes = minutes % 60;
                return hours + "ч " + remainingMinutes + "м";
            }
            return minutes + " минут";
        }
        
        public String getFormattedResources() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Material, Integer> entry : requiredResources.entrySet()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(entry.getValue()).append("x ").append(entry.getKey().name());
            }
            return sb.toString();
        }
    }
}
