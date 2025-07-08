package com.uranium.managers;

import com.uranium.UraniumCraft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class EnhancedVisualEffectsManager extends VisualEffectsManager {
    
    private final Map<String, BukkitRunnable> persistentEffects = new HashMap<>();
    
    public EnhancedVisualEffectsManager(UraniumCraft plugin) {
        super(plugin);
    }
    
    // Улучшенные эффекты для GUI
    public void playGUIOpenEffect(Player player, String guiType) {
        Location loc = player.getLocation().add(0, 2, 0);
        
        switch (guiType) {
            case "main":
                createSpiralParticleEffect(loc, Particle.ENCHANT, 40, 2.0, 3.0);
                player.playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1.5f);
                break;
            case "catalog":
                createBookOpenEffect(loc);
                player.playSound(loc, Sound.ITEM_BOOK_PAGE_TURN, 0.7f, 1.2f);
                break;
            case "achievements":
                createFireworkEffect(loc);
                player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.0f);
                break;
            case "admin":
                createWarningEffect(loc);
                player.playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 0.8f);
                break;
        }
    }
    
    private void createSpiralParticleEffect(Location center, Particle particle, int duration, double radius, double height) {
        new BukkitRunnable() {
            private int ticks = 0;
            private double angle = 0;
            
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                
                for (double y = 0; y < height; y += 0.2) {
                    double currentAngle = angle + (y * 0.5);
                    double currentRadius = radius * (1 - y / height);
                    double x = Math.cos(currentAngle) * currentRadius;
                    double z = Math.sin(currentAngle) * currentRadius;
                    
                    Location particleLoc = center.clone().add(x, y, z);
                    center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
                }
                
                angle += 0.3;
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    private void createBookOpenEffect(Location center) {
        new BukkitRunnable() {
            private int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 30) {
                    cancel();
                    return;
                }
                
                // Страницы книги
                for (int i = 0; i < 8; i++) {
                    double angle = (i * Math.PI / 4) + (ticks * 0.1);
                    double x = Math.cos(angle) * 1.5;
                    double z = Math.sin(angle) * 1.5;
                    
                    Location particleLoc = center.clone().add(x, 0.5, z);
                    center.getWorld().spawnParticle(Particle.ENCHANT, particleLoc, 1, 0, 0, 0, 0.5);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    private void createFireworkEffect(Location center) {
        new BukkitRunnable() {
            private int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 20) {
                    cancel();
                    return;
                }
                
                // Взрыв фейерверка
                for (int i = 0; i < 12; i++) {
                    double angle = i * Math.PI / 6;
                    double x = Math.cos(angle) * (ticks * 0.1);
                    double z = Math.sin(angle) * (ticks * 0.1);
                    double y = Math.sin(ticks * 0.2) * 0.5;
                    
                    Location particleLoc = center.clone().add(x, y, z);
                    center.getWorld().spawnParticle(Particle.FIREWORK, particleLoc, 1, 0, 0, 0, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void createWarningEffect(Location center) {
        new BukkitRunnable() {
            private int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 40) {
                    cancel();
                    return;
                }
                
                // Мигающий красный эффект
                if (ticks % 10 < 5) {
                    center.getWorld().spawnParticle(Particle.DUST, center, 10, 0.5, 0.5, 0.5, 0);
                }
                
                // Энергетический щит
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4;
                    double x = Math.cos(angle) * 2;
                    double z = Math.sin(angle) * 2;
                    
                    Location particleLoc = center.clone().add(x, 1, z);
                    center.getWorld().spawnParticle(Particle.ENCHANT, particleLoc, 1, 0, 0, 0, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    // Улучшенные эффекты радиации
    public void createRadiationAura(Player player, int radiationLevel) {
        String key = "radiation_aura_" + player.getUniqueId();
        stopPersistentEffect(key);
        
        if (radiationLevel < 50) return;
        
        BukkitRunnable aura = new BukkitRunnable() {
            private double angle = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || plugin.getRadiationManager().getPlayerRadiation(player) < 50) {
                    cancel();
                    return;
                }
                
                Location loc = player.getLocation().add(0, 1, 0);
                int intensity = Math.min(radiationLevel / 50, 10);
                
                // Вращающиеся частицы радиации
                for (int i = 0; i < intensity; i++) {
                    double currentAngle = angle + (i * Math.PI / intensity);
                    double x = Math.cos(currentAngle) * 1.5;
                    double z = Math.sin(currentAngle) * 1.5;
                    
                    Location particleLoc = loc.clone().add(x, 0, z);
                    player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0.1, 0.1, 0.1, 0);
                }
                
                // Дополнительные эффекты для высоких уровней
                if (radiationLevel >= 300) {
                    player.getWorld().spawnParticle(Particle.SMOKE, loc, 3, 0.3, 0.3, 0.3, 0.02);
                }
                
                if (radiationLevel >= 500) {
                    player.getWorld().spawnParticle(Particle.FLAME, loc, 2, 0.2, 0.2, 0.2, 0.01);
                }
                
                angle += 0.2;
            }
        };
        
        aura.runTaskTimer(plugin, 0L, 5L);
        persistentEffects.put(key, aura);
    }
    
    // Эффекты для структур с улучшенной анимацией
    public void createAdvancedCentrifugeEffect(Location location) {
        String key = "centrifuge_" + location.hashCode();
        stopPersistentEffect(key);
        
        BukkitRunnable effect = new BukkitRunnable() {
            private double angle = 0;
            private int phase = 0;
            
            @Override
            public void run() {
                if (!plugin.getCentrifugeManager().isCentrifuge(location)) {
                    cancel();
                    return;
                }
                
                var data = plugin.getCentrifugeManager().getCentrifugeData(location);
                if (data == null || !data.isRunning()) {
                    cancel();
                    return;
                }
                
                // Основные вращающиеся частицы
                for (int i = 0; i < 12; i++) {
                    double currentAngle = angle + (i * Math.PI / 6);
                    double radius = 1.5 + Math.sin(phase * 0.1) * 0.3;
                    double x = Math.cos(currentAngle) * radius;
                    double z = Math.sin(currentAngle) * radius;
                    double y = 1.5 + Math.sin(currentAngle + phase * 0.05) * 0.2;
                    
                    Location particleLoc = location.clone().add(x, y, z);
                    location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, particleLoc, 1, 0, 0, 0, 0);
                    
                    // Дополнительные частицы для красоты
                    if (i % 3 == 0) {
                        location.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0);
                    }
                }
                
                // Центральный столб энергии
                for (double y = 0.5; y <= 2.5; y += 0.2) {
                    Location centerLoc = location.clone().add(0.5, y, 0.5);
                    location.getWorld().spawnParticle(Particle.FLAME, centerLoc, 1, 0.1, 0.1, 0.1, 0);
                }
                
                // Пульсирующий эффект каждые 2 секунды
                if (phase % 40 == 0) {
                    location.getWorld().spawnParticle(Particle.EXPLOSION, 
                        location.clone().add(0.5, 1, 0.5), 1);
                    location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.2f, 2.0f);
                }
                
                angle += 0.3;
                phase++;
            }
        };
        
        effect.runTaskTimer(plugin, 0L, 2L);
        persistentEffects.put(key, effect);
    }
    
    // Голографические информационные панели
    public void createInfoHologram(Location location, String title, String[] info, int duration) {
        new BukkitRunnable() {
            private int ticks = 0;
            private double height = 0;
            
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                
                Location holoLoc = location.clone().add(0.5, 3 + height, 0.5);
                
                // Создаем голографический эффект
                holoLoc.getWorld().spawnParticle(Particle.ENCHANT, holoLoc, 8, 0.3, 0.1, 0.3, 0.5);
                holoLoc.getWorld().spawnParticle(Particle.END_ROD, holoLoc, 3, 0.2, 0.2, 0.2, 0);
                
                // Рамка вокруг голограммы
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4;
                    double x = Math.cos(angle) * 0.8;
                    double z = Math.sin(angle) * 0.8;
                    
                    Location frameLoc = holoLoc.clone().add(x, 0, z);
                    holoLoc.getWorld().spawnParticle(Particle.DUST, frameLoc, 1, 0, 0, 0, 0);
                }
                
                height = Math.sin(ticks * 0.1) * 0.3;
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 3L);
    }
    
    public void stopPersistentEffect(String key) {
        BukkitRunnable effect = persistentEffects.remove(key);
        if (effect != null && !effect.isCancelled()) {
            effect.cancel();
        }
    }
    
    @Override
    public void stopAllEffects() {
        super.stopAllEffects();
        
        for (BukkitRunnable effect : persistentEffects.values()) {
            if (!effect.isCancelled()) {
                effect.cancel();
            }
        }
        persistentEffects.clear();
    }
}
