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
import java.util.UUID;

public class VisualEffectsManager {
    
    protected final UraniumCraft plugin;
    private final Map<Location, BukkitRunnable> activeEffects = new HashMap<>();
    
    public VisualEffectsManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    // Эффекты центрифуги
    public void startCentrifugeEffects(Location location) {
        stopEffects(location);
        
        BukkitRunnable effect = new BukkitRunnable() {
            private double angle = 0;
            private int ticks = 0;
            
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
                
                // Вращающиеся частицы
                for (int i = 0; i < 8; i++) {
                    double currentAngle = angle + (i * Math.PI / 4);
                    double x = Math.cos(currentAngle) * 1.5;
                    double z = Math.sin(currentAngle) * 1.5;
                    
                    Location particleLoc = location.clone().add(x, 1.5, z);
                    location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, particleLoc, 1, 0, 0, 0, 0);
                    location.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0);
                }
                
                // Центральные частицы
                location.getWorld().spawnParticle(Particle.FLAME, location.clone().add(0.5, 1, 0.5), 3, 0.2, 0.2, 0.2, 0);
                
                // Звуки каждые 2 секунды
                if (ticks % 40 == 0) {
                    location.getWorld().playSound(location, Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.3f, 1.2f);
                }
                
                // Интенсивные эффекты каждые 5 секунд
                if (ticks % 100 == 0) {
                    location.getWorld().spawnParticle(Particle.EXPLOSION, location.clone().add(0.5, 1, 0.5), 1);
                    location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.2f, 2.0f);
                }
                
                angle += 0.2;
                ticks++;
            }
        };
        
        effect.runTaskTimer(plugin, 0L, 2L);
        activeEffects.put(location, effect);
    }
    
    // Эффекты телепорта
    public void startTeleporterEffects(Location location) {
        stopEffects(location);
        
        BukkitRunnable effect = new BukkitRunnable() {
            private double phase = 0;
            
            @Override
            public void run() {
                if (!plugin.getTeleporterManager().isTeleporter(location)) {
                    cancel();
                    return;
                }
                
                // Пульсирующие частицы
                double intensity = Math.sin(phase) * 0.5 + 0.5;
                int particleCount = (int) (10 * intensity) + 5;
                
                location.getWorld().spawnParticle(Particle.PORTAL, 
                    location.clone().add(0.5, 1, 0.5), 
                    particleCount, 0.5, 0.5, 0.5, 0.1);
                
                location.getWorld().spawnParticle(Particle.END_ROD, 
                    location.clone().add(0.5, 2, 0.5), 
                    3, 0.3, 0.3, 0.3, 0);
                
                // Звук каждые 3 секунды
                if (phase % (Math.PI * 3) < 0.1) {
                    location.getWorld().playSound(location, Sound.BLOCK_PORTAL_AMBIENT, 0.3f, 1.5f);
                }
                
                phase += 0.1;
            }
        };
        
        effect.runTaskTimer(plugin, 0L, 5L);
        activeEffects.put(location, effect);
    }
    
    // Эффекты лаборатории
    public void startLaboratoryEffects(Location location) {
        stopEffects(location);
        
        BukkitRunnable effect = new BukkitRunnable() {
            private int ticks = 0;
            
            @Override
            public void run() {
                if (!plugin.getLaboratoryManager().isLaboratory(location)) {
                    cancel();
                    return;
                }
                
                var data = plugin.getLaboratoryManager().getLaboratoryData(location);
                if (data == null) {
                    cancel();
                    return;
                }
                
                // Эффекты зависят от активности лаборатории
                if (data.hasActiveResearch()) {
                    // Активное исследование
                    location.getWorld().spawnParticle(Particle.ENCHANT, 
                        location.clone().add(0.5, 1.5, 0.5), 
                        15, 0.5, 0.5, 0.5, 1);
                    
                    location.getWorld().spawnParticle(Particle.WITCH, 
                        location.clone().add(0.5, 1, 0.5), 
                        5, 0.3, 0.3, 0.3, 0);
                    
                    if (ticks % 60 == 0) {
                        location.getWorld().playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.0f);
                    }
                } else {
                    // Режим ожидания
                    location.getWorld().spawnParticle(Particle.ENCHANT, 
                        location.clone().add(0.5, 1.2, 0.5), 
                        3, 0.2, 0.2, 0.2, 0.5);
                }
                
                ticks++;
            }
        };
        
        effect.runTaskTimer(plugin, 0L, 10L);
        activeEffects.put(location, effect);
    }
    
    // Эффекты радиации для игрока
    public void applyRadiationEffects(Player player, int radiationLevel) {
        if (radiationLevel < 50) return;
        
        Location loc = player.getLocation().add(0, 1, 0);
        
        // Интенсивность зависит от уровня радиации
        int particleCount = Math.min(radiationLevel / 10, 20);
        
        if (radiationLevel >= 50) {
            player.getWorld().spawnParticle(Particle.SMOKE, loc, particleCount, 0.5, 0.5, 0.5, 0.02);
        }
        
        if (radiationLevel >= 150) {
            player.getWorld().spawnParticle(Particle.CLOUD, loc, particleCount / 2, 0.3, 0.3, 0.3, 0.01);
        }
        
        if (radiationLevel >= 300) {
            player.getWorld().spawnParticle(Particle.DUST, loc, particleCount / 3, 0.4, 0.4, 0.4, 0.05);
        }
        
        if (radiationLevel >= 500) {
            player.getWorld().spawnParticle(Particle.FLAME, loc, particleCount / 4, 0.2, 0.2, 0.2, 0.02);
        }
    }
    
    // Эффекты рельсотрона
    public void playRailgunEffect(Player player, String mode) {
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection();
        
        switch (mode) {
            case "Single":
                createEnergyBeam(start, direction, 20, Particle.ELECTRIC_SPARK);
                player.getWorld().playSound(start, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 2.0f);
                break;
                
            case "Burst":
                for (int i = 0; i < 3; i++) {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        createEnergyBeam(start, direction, 15, Particle.FLAME);
                        player.getWorld().playSound(start, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.7f, 1.8f);
                    }, i * 3L);
                }
                break;
                
            case "Piercing":
                createEnergyBeam(start, direction, 25, Particle.END_ROD);
                player.getWorld().playSound(start, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.2f, 1.5f);
                break;
        }
    }
    
    private void createEnergyBeam(Location start, Vector direction, int length, Particle particle) {
        new BukkitRunnable() {
            private int distance = 0;
            
            @Override
            public void run() {
                if (distance >= length) {
                    cancel();
                    return;
                }
                
                Location current = start.clone().add(direction.clone().multiply(distance));
                
                // Проверка на препятствия
                if (current.getBlock().getType() != Material.AIR) {
                    // Эффект попадания
                    current.getWorld().spawnParticle(Particle.EXPLOSION, current, 5);
                    cancel();
                    return;
                }
                
                current.getWorld().spawnParticle(particle, current, 3, 0.1, 0.1, 0.1, 0);
                distance++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    // Эффекты силовой брони
    public void playPowerArmorEffect(Player player, String mode) {
        Location loc = player.getLocation().add(0, 1, 0);
        
        switch (mode) {
            case "Protection":
                player.getWorld().spawnParticle(Particle.ENCHANT, loc, 10, 0.5, 0.5, 0.5, 0);
                player.getWorld().playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.2f);
                break;
                
            case "Speed":
                player.getWorld().spawnParticle(Particle.CLOUD, loc, 15, 0.3, 0.3, 0.3, 0.1);
                player.getWorld().playSound(loc, Sound.ENTITY_HORSE_GALLOP, 1.0f, 1.5f);
                break;
                
            case "Jump":
                player.getWorld().spawnParticle(Particle.FIREWORK, loc, 20, 0.2, 0.2, 0.2, 0.1);
                player.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.8f);
                break;
                
            default:
                player.getWorld().spawnParticle(Particle.ENCHANT, loc, 8, 0.4, 0.4, 0.4, 0.5);
                player.getWorld().playSound(loc, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
                break;
        }
    }
    
    // Эффекты телепортации
    public void playTeleportEffect(Location from, Location to) {
        // Эффект отправления
        from.getWorld().spawnParticle(Particle.PORTAL, from.add(0.5, 1, 0.5), 50, 0.5, 1, 0.5, 1);
        from.getWorld().spawnParticle(Particle.REVERSE_PORTAL, from, 30, 0.3, 0.5, 0.3, 0.5);
        from.getWorld().playSound(from, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
        
        // Эффект прибытия (с задержкой)
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            to.getWorld().spawnParticle(Particle.PORTAL, to.add(0.5, 1, 0.5), 50, 0.5, 1, 0.5, 1);
            to.getWorld().spawnParticle(Particle.END_ROD, to, 20, 0.4, 0.8, 0.4, 0.1);
            to.getWorld().playSound(to, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
        }, 10L);
    }
    
    // Эффекты планшета
    public void playTabletEffect(Player player, String action) {
        Location loc = player.getLocation().add(0, 2, 0);
        
        switch (action) {
            case "open":
                player.getWorld().spawnParticle(Particle.ENCHANT, loc, 20, 0.5, 0.5, 0.5, 1);
                player.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 2.0f);
                break;
                
            case "scan":
                player.getWorld().spawnParticle(Particle.END_ROD, loc, 10, 0.3, 0.3, 0.3, 0.1);
                player.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                break;
        }
    }
    
    // Голографические эффекты
    public void createHologram(Location location, String text, int duration) {
        new BukkitRunnable() {
            private int ticks = 0;
            private double height = 0;
            
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                
                Location holoLoc = location.clone().add(0.5, 2 + height, 0.5);
                
                // Создаем голографический текст с частицами
                holoLoc.getWorld().spawnParticle(Particle.ENCHANT, holoLoc, 5, 0.2, 0.1, 0.2, 0.5);
                holoLoc.getWorld().spawnParticle(Particle.END_ROD, holoLoc, 2, 0.1, 0.1, 0.1, 0);
                
                height = Math.sin(ticks * 0.1) * 0.2;
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    // Остановка эффектов
    public void stopEffects(Location location) {
        BukkitRunnable effect = activeEffects.remove(location);
        if (effect != null && !effect.isCancelled()) {
            effect.cancel();
        }
    }
    
    public void stopAllEffects() {
        for (BukkitRunnable effect : activeEffects.values()) {
            if (!effect.isCancelled()) {
                effect.cancel();
            }
        }
        activeEffects.clear();
    }
}
