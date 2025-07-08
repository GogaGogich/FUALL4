package com.uranium.utils;

import com.uranium.UraniumCraft;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final UraniumCraft plugin;
    private FileConfiguration config;
    
    public ConfigManager(UraniumCraft plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    private void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }
    
    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    // Методы для получения настроек
    public int getRadiationUpdateInterval() {
        return config.getInt("radiation.update-interval", 100);
    }
    
    public int getCentrifugeProcessingTime() {
        return config.getInt("centrifuge.processing-time", 300);
    }
    
    public int getLaboratoryEnergyRegenRate() {
        return config.getInt("laboratory.energy-regen-rate", 10);
    }
    
    public int getTeleporterMaxEnergy() {
        return config.getInt("teleporter.max-energy", 5000);
    }
    
    public boolean isRadiationEnabled() {
        return config.getBoolean("radiation.enabled", true);
    }
    
    public boolean isVisualEffectsEnabled() {
        return config.getBoolean("visual-effects.enabled", true);
    }
    
    public int getRailgunMaxEnergy() {
        return config.getInt("railgun.max-energy", 1000);
    }
    
    public int getPowerArmorMaxEnergy() {
        return config.getInt("power-armor.max-energy", 1000);
    }
    
    public int getUraniumTabletMaxEnergy() {
        return config.getInt("uranium-tablet.max-energy", 2000);
    }
}
