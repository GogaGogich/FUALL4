package com.uranium.managers;

import com.uranium.UraniumCraft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    
    private final UraniumCraft plugin;
    private final Map<String, BukkitRunnable> activeAnimations = new HashMap<>();
    
    public AnimationManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    // Анимация энергетического щита
    public void playShieldAnimation(Player player, int duration) {
        String key = "shield_" + player.getUniqueId();
        stopAnimation(key);
        
        BukkitRunnable animation = new BukkitRunnable() {
            private int ticks = 0;
            private double angle = 0;
            
            @Override
            public void run() {
                if (ticks >= duration || !player.isOnline()) {
                    cancel();
                    return;
                }
                
                Location center = player.getLocation().add(0, 1, 0);
                
                // Создаем энергетический щит вокруг игрока
                for (int i = 0; i < 12; i++) {
                    double currentAngle = angle + (i * Math.PI / 6);
                    double x = Math.cos(currentAngle) * 2;
                    double z = Math.sin(currentAngle) * 2;
                    
                    Location particleLoc = center.clone().add(x, 0, z);
                    player.getWorld().spawnParticle(Particle.ENCHANT, particleLoc, 1, 0, 0, 0, 0);
                }
                
                angle += 0.3;
                ticks++;
            }
        };
        
        animation.runTaskTimer(plugin, 0L, 2L);
        activeAnimations.put(key, animation);
    }
    
    // Анимация энергетического луча
    public void playBeamAnimation(Location start, Location end, Particle particle, int duration) {
        String key = "beam_" + start.hashCode() + "_" + end.hashCode();
        stopAnimation(key);
        
        BukkitRunnable animation = new BukkitRunnable() {
            private int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                
                Vector direction = end.toVector().subtract(start.toVector()).normalize();
                double distance = start.distance(end);
                
                for (double d = 0; d < distance; d += 0.5) {
                    Location current = start.clone().add(direction.clone().multiply(d));
                    current.getWorld().spawnParticle(particle, current, 1, 0.1, 0.1, 0.1, 0);
                }
                
                ticks++;
            }
        };
        
        animation.runTaskTimer(plugin, 0L, 1L);
        activeAnimations.put(key, animation);
    }
    
    // Анимация взрыва энергии
    public void playEnergyExplosion(Location center, int radius, Particle particle) {
        new BukkitRunnable() {
            private double currentRadius = 0;
            
            @Override
            public void run() {
                if (currentRadius >= radius) {
                    cancel();
                    return;
                }
                
                // Создаем сферу частиц
                for (double phi = 0; phi < Math.PI; phi += Math.PI / 10) {
                    for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 10) {
                        double x = currentRadius * Math.sin(phi) * Math.cos(theta);
                        double y = currentRadius * Math.cos(phi);
                        double z = currentRadius * Math.sin(phi) * Math.sin(theta);
                        
                        Location particleLoc = center.clone().add(x, y, z);
                        center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
                    }
                }
                
                currentRadius += 0.5;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    // Анимация спирали
    public void playSpiralAnimation(Location center, int height, Particle particle, int duration) {
        String key = "spiral_" + center.hashCode();
        stopAnimation(key);
        
        BukkitRunnable animation = new BukkitRunnable() {
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
                    double x = Math.cos(currentAngle) * (2 - y / height);
                    double z = Math.sin(currentAngle) * (2 - y / height);
                    
                    Location particleLoc = center.clone().add(x, y, z);
                    center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
                }
                
                angle += 0.2;
                ticks++;
            }
        };
        
        animation.runTaskTimer(plugin, 0L, 2L);
        activeAnimations.put(key, animation);
    }
    
    // Анимация пульсации
    public void playPulseAnimation(Location center, Particle particle, int maxRadius, int duration) {
        String key = "pulse_" + center.hashCode();
        stopAnimation(key);
        
        BukkitRunnable animation = new BukkitRunnable() {
            private int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                
                double radius = Math.sin(ticks * 0.2) * maxRadius;
                int particleCount = (int) (radius * 4) + 5;
                
                center.getWorld().spawnParticle(particle, center, particleCount, radius, 0.5, radius, 0);
                
                ticks++;
            }
        };
        
        animation.runTaskTimer(plugin, 0L, 3L);
        activeAnimations.put(key, animation);
    }
    
    // Анимация вихря
    public void playVortexAnimation(Location center, int height, Particle particle, int duration) {
        String key = "vortex_" + center.hashCode();
        stopAnimation(key);
        
        BukkitRunnable animation = new BukkitRunnable() {
            private int ticks = 0;
            private double angle = 0;
            
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                
                for (double y = 0; y < height; y += 0.3) {
                    double radius = (height - y) / height * 2;
                    double currentAngle = angle + (y * 0.8);
                    
                    for (int i = 0; i < 3; i++) {
                        double spiralAngle = currentAngle + (i * Math.PI * 2 / 3);
                        double x = Math.cos(spiralAngle) * radius;
                        double z = Math.sin(spiralAngle) * radius;
                        
                        Location particleLoc = center.clone().add(x, y, z);
                        center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
                    }
                }
                
                angle += 0.4;
                ticks++;
            }
        };
        
        animation.runTaskTimer(plugin, 0L, 1L);
        activeAnimations.put(key, animation);
    }
    
    // Анимация молнии
    public void playLightningAnimation(Location start, Location end, int segments) {
        Vector direction = end.toVector().subtract(start.toVector());
        double totalDistance = start.distance(end);
        
        Location current = start.clone();
        
        for (int i = 0; i < segments; i++) {
            // Добавляем случайное отклонение для эффекта молнии
            Vector segment = direction.clone().multiply(1.0 / segments);
            segment.add(new Vector(
                (Math.random() - 0.5) * 0.5,
                (Math.random() - 0.5) * 0.5,
                (Math.random() - 0.5) * 0.5
            ));
            
            current.add(segment);
            current.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, current, 3, 0.1, 0.1, 0.1, 0);
        }
    }
    
    public void stopAnimation(String key) {
        BukkitRunnable animation = activeAnimations.remove(key);
        if (animation != null && !animation.isCancelled()) {
            animation.cancel();
        }
    }
    
    public void stopAllAnimations() {
        for (BukkitRunnable animation : activeAnimations.values()) {
            if (!animation.isCancelled()) {
                animation.cancel();
            }
        }
        activeAnimations.clear();
    }
}
