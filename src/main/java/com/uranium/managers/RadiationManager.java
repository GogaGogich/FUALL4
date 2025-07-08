package com.uranium.managers;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RadiationManager {
    
    private final UraniumCraft plugin;
    private final Map<UUID, Integer> playerRadiation = new ConcurrentHashMap<>();
    private final Map<Material, Integer> radioactiveMaterials = new HashMap<>();
    
    public RadiationManager(UraniumCraft plugin) {
        this.plugin = plugin;
        initializeRadioactiveMaterials();
    }
    
    private void initializeRadioactiveMaterials() {
        // Урановые материалы и их уровни радиации
        radioactiveMaterials.put(Material.GUNPOWDER, 5);  // Урановая пыль
        radioactiveMaterials.put(Material.IRON_INGOT, 15); // Урановый слиток
        radioactiveMaterials.put(Material.IRON_BLOCK, 50); // Урановый блок
    }
    
    public void updateAllPlayersRadiation() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            updatePlayerRadiation(player);
        }
    }
    
    public void updatePlayerRadiation(Player player) {
        if (player.hasPermission("uraniumcraft.radiation.immune")) {
            return;
        }
        
        int totalRadiation = calculatePlayerRadiation(player);
        UUID playerId = player.getUniqueId();
        
        playerRadiation.put(playerId, totalRadiation);
        
        // Применение эффектов радиации
        applyRadiationEffects(player, totalRadiation);
        
        // Обновление отображения радиации
        updateRadiationDisplay(player, totalRadiation);
    }
    
    private int calculatePlayerRadiation(Player player) {
        int totalRadiation = 0;
        
        // Проверка инвентаря
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && radioactiveMaterials.containsKey(item.getType())) {
                totalRadiation += radioactiveMaterials.get(item.getType()) * item.getAmount();
            }
        }
        
        // Проверка экипированных предметов (в 3 раза больше радиации)
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack item : armor) {
            if (item != null && radioactiveMaterials.containsKey(item.getType())) {
                totalRadiation += radioactiveMaterials.get(item.getType()) * item.getAmount() * 3;
            }
        }
        
        // Проверка химзащиты (снижает радиацию)
        int protectionLevel = getProtectionLevel(player);
        totalRadiation = Math.max(0, totalRadiation - (totalRadiation * protectionLevel / 100));
        
        return totalRadiation;
    }
    
    private int getProtectionLevel(Player player) {
        int protection = 0;
        ItemStack[] armor = player.getInventory().getArmorContents();
        
        for (ItemStack item : armor) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String displayName = item.getItemMeta().getDisplayName();
                if (displayName.contains("Химзащита") || displayName.contains("Силовая броня")) {
                    protection += 25; // Каждый элемент дает 25% защиты
                }
            }
        }
        
        return Math.min(100, protection);
    }
    
    private void applyRadiationEffects(Player player, int radiation) {
        // Очистка старых эффектов радиации
        clearRadiationEffects(player);
        
        RadiationLevel level = getRadiationLevel(radiation);
        
        switch (level) {
            case LOW:
                // Нет негативных эффектов
                break;
            case MEDIUM:
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120, 0));
                break;
            case HIGH:
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 120, 0));
                break;
            case CRITICAL:
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120, 0));
                break;
            case DEADLY:
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 120, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
                break;
            case EXTREME:
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 1));
                // Прямой урон
                player.damage(2.0);
                break;
        }
    }
    
    private void clearRadiationEffects(Player player) {
        player.removePotionEffect(PotionEffectType.HUNGER);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.NAUSEA);
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.WITHER);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }
    
    private void updateRadiationDisplay(Player player, int radiation) {
        RadiationLevel level = getRadiationLevel(radiation);
        String color = getRadiationColor(level);
        String levelName = getRadiationLevelName(level);
        
        player.sendActionBar(color + "☢ Радиация: " + radiation + " рад (" + levelName + ")");
    }
    
    public RadiationLevel getRadiationLevel(int radiation) {
        if (radiation == 0) return RadiationLevel.NORMAL;
        if (radiation < 50) return RadiationLevel.LOW;
        if (radiation < 150) return RadiationLevel.MEDIUM;
        if (radiation < 300) return RadiationLevel.HIGH;
        if (radiation < 500) return RadiationLevel.CRITICAL;
        if (radiation < 750) return RadiationLevel.DEADLY;
        return RadiationLevel.EXTREME;
    }
    
    private String getRadiationColor(RadiationLevel level) {
        return switch (level) {
            case NORMAL -> "§a";
            case LOW -> "§e";
            case MEDIUM -> "§6";
            case HIGH -> "§c";
            case CRITICAL -> "§4";
            case DEADLY -> "§4";
            case EXTREME -> "§0";
        };
    }
    
    private String getRadiationLevelName(RadiationLevel level) {
        return switch (level) {
            case NORMAL -> "НОРМА";
            case LOW -> "НИЗКИЙ";
            case MEDIUM -> "СРЕДНИЙ";
            case HIGH -> "ВЫСОКИЙ";
            case CRITICAL -> "КРИТИЧЕСКИЙ";
            case DEADLY -> "СМЕРТЕЛЬНЫЙ";
            case EXTREME -> "КРАЙНИЙ";
        };
    }
    
    public int getPlayerRadiation(Player player) {
        return playerRadiation.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void setPlayerRadiation(Player player, int radiation) {
        playerRadiation.put(player.getUniqueId(), radiation);
    }
    
    public void clearPlayerRadiation(Player player) {
        playerRadiation.remove(player.getUniqueId());
        clearRadiationEffects(player);
    }
    
    public void saveAllData() {
        // Сохранение данных о радиации игроков
        // Реализация сохранения в файл
    }
    
    public void reload() {
        // Перезагрузка настроек радиации
        initializeRadioactiveMaterials();
    }
    
    public enum RadiationLevel {
        NORMAL, LOW, MEDIUM, HIGH, CRITICAL, DEADLY, EXTREME
    }
}
