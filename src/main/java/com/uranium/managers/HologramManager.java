package com.uranium.managers;

import com.uranium.UraniumCraft;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {
    
    private final UraniumCraft plugin;
    private final Map<UUID, ArmorStand> holograms = new HashMap<>();
    private final Map<UUID, BukkitRunnable> hologramTasks = new HashMap<>();
    
    public HologramManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    public UUID createFloatingHologram(Location location, String text, int durationTicks) {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setCustomName(text);
        hologram.setCustomNameVisible(true);
        hologram.setMarker(true);
        
        UUID id = UUID.randomUUID();
        holograms.put(id, hologram);
        
        // Анимация плавания
        BukkitRunnable task = new BukkitRunnable() {
            private int ticks = 0;
            private double initialY = location.getY();
            
            @Override
            public void run() {
                if (ticks >= durationTicks || !hologram.isValid()) {
                    removeHologram(id);
                    cancel();
                    return;
                }
                
                // Плавное движение вверх-вниз
                double newY = initialY + Math.sin(ticks * 0.1) * 0.3;
                Location newLoc = hologram.getLocation();
                newLoc.setY(newY);
                hologram.teleport(newLoc);
                
                // Частицы вокруг голограммы
                hologram.getWorld().spawnParticle(Particle.ENCHANT, 
                    hologram.getLocation().add(0, 1, 0), 2, 0.2, 0.2, 0.2, 0.1);
                
                ticks++;
            }
        };
        
        task.runTaskTimer(plugin, 0L, 1L);
        hologramTasks.put(id, task);
        
        return id;
    }
    
    public UUID createStaticHologram(Location location, String text) {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setCustomName(text);
        hologram.setCustomNameVisible(true);
        hologram.setMarker(true);
        
        UUID id = UUID.randomUUID();
        holograms.put(id, hologram);
        
        return id;
    }
    
    public void updateHologramText(UUID id, String newText) {
        ArmorStand hologram = holograms.get(id);
        if (hologram != null && hologram.isValid()) {
            hologram.setCustomName(newText);
        }
    }
    
    public void removeHologram(UUID id) {
        ArmorStand hologram = holograms.remove(id);
        if (hologram != null && hologram.isValid()) {
            hologram.remove();
        }
        
        BukkitRunnable task = hologramTasks.remove(id);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    public void removeAllHolograms() {
        for (UUID id : holograms.keySet()) {
            removeHologram(id);
        }
        holograms.clear();
        hologramTasks.clear();
    }
    
    public boolean hologramExists(UUID id) {
        ArmorStand hologram = holograms.get(id);
        return hologram != null && hologram.isValid();
    }
}
