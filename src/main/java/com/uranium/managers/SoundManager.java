package com.uranium.managers;

import com.uranium.UraniumCraft;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    
    private final UraniumCraft plugin;
    private final Map<String, BukkitRunnable> ambientSounds = new HashMap<>();
    
    public SoundManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    // Звуки радиации
    public void playRadiationSound(Player player, int radiationLevel) {
        if (radiationLevel < 50) return;
        
        // Частота и высота звука зависят от уровня радиации
        float volume = Math.min(1.0f, radiationLevel / 500.0f);
        float pitch = 0.5f + (radiationLevel / 1000.0f);
        
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, volume, pitch);
        
        if (radiationLevel >= 300) {
            player.playSound(player.getLocation(), Sound.ENTITY_GHAST_AMBIENT, volume * 0.3f, 2.0f);
        }
    }
    
    // Амбиентные звуки для структур
    public void startAmbientSound(String key, Location location, Sound sound, float volume, float pitch, int interval) {
        stopAmbientSound(key);
        
        BukkitRunnable ambient = new BukkitRunnable() {
            @Override
            public void run() {
                location.getWorld().playSound(location, sound, volume, pitch);
            }
        };
        
        ambient.runTaskTimer(plugin, 0L, interval);
        ambientSounds.put(key, ambient);
    }
    
    public void stopAmbientSound(String key) {
        BukkitRunnable ambient = ambientSounds.remove(key);
        if (ambient != null && !ambient.isCancelled()) {
            ambient.cancel();
        }
    }
    
    // Звуковые последовательности
    public void playSequence(Location location, Sound[] sounds, float[] volumes, float[] pitches, int[] delays) {
        for (int i = 0; i < sounds.length; i++) {
            final int index = i;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                location.getWorld().playSound(location, sounds[index], volumes[index], pitches[index]);
            }, delays[i]);
        }
    }
    
    // Специальные звуковые эффекты
    public void playPowerUpSound(Player player) {
        Sound[] sounds = {Sound.BLOCK_NOTE_BLOCK_PLING, Sound.BLOCK_NOTE_BLOCK_CHIME, Sound.BLOCK_BEACON_POWER_SELECT};
        float[] volumes = {0.5f, 0.7f, 1.0f};
        float[] pitches = {1.0f, 1.5f, 2.0f};
        int[] delays = {0, 5, 10};
        
        playSequence(player.getLocation(), sounds, volumes, pitches, delays);
    }
    
    public void playErrorSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
    }
    
    public void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
    }
    
    public void stopAllAmbientSounds() {
        for (BukkitRunnable ambient : ambientSounds.values()) {
            if (!ambient.isCancelled()) {
                ambient.cancel();
            }
        }
        ambientSounds.clear();
    }
}
