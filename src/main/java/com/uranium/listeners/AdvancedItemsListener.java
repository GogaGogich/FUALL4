package com.uranium.listeners;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdvancedItemsListener implements Listener {
    
    private final UraniumCraft plugin;
    private final Map<UUID, Long> lastUse = new HashMap<>();
    private final Map<UUID, String> powerArmorModes = new HashMap<>();
    private final Map<UUID, String> railgunModes = new HashMap<>();
    
    public AdvancedItemsListener(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        
        // Проверка кулдауна
        if (hasRecentlyUsed(player)) {
            return;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        
        if (displayName.contains("Рельсотрон")) {
            handleRailgun(player, item, event);
        } else if (displayName.contains("Урановый планшет")) {
            handleUraniumTablet(player, item, event);
        } else if (displayName.contains("Счетчик Гейгера")) {
            handleGeigerCounter(player, item, event);
        }
    }
    
    private void handleRailgun(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        
        if (player.isSneaking()) {
            // Смена режима
            switchRailgunMode(player, item);
        } else {
            // Стрельба
            fireRailgun(player, item);
        }
        
        setLastUse(player);
    }
    
    private void switchRailgunMode(Player player, ItemStack item) {
        String currentMode = railgunModes.getOrDefault(player.getUniqueId(), "Single");
        String newMode = switch (currentMode) {
            case "Single" -> "Burst";
            case "Burst" -> "Piercing";
            case "Piercing" -> "Single";
            default -> "Single";
        };
        
        railgunModes.put(player.getUniqueId(), newMode);
        
        // Обновление лора предмета
        updateRailgunLore(item, newMode);
        
        MessageUtil.sendMessage(player, "&aРежим рельсотрона изменен на: &e" + newMode);
        
        // Визуальные и звуковые эффекты
        plugin.getVisualEffectsManager().playRailgunEffect(player, "mode_switch");
        plugin.getSoundManager().playSuccessSound(player);
        
        plugin.getPlayerStatsManager().addPowerArmorModeChange(player);
    }
    
    private void fireRailgun(Player player, ItemStack item) {
        String mode = railgunModes.getOrDefault(player.getUniqueId(), "Single");
        
        // Визуальные и звуковые эффекты стрельбы
        plugin.getVisualEffectsManager().playRailgunEffect(player, mode);
        
        switch (mode) {
            case "Single":
                fireSingleShot(player);
                break;
            case "Burst":
                fireBurstShot(player);
                break;
            case "Piercing":
                firePiercingShot(player);
                break;
        }
        
        plugin.getPlayerStatsManager().addRailgunShot(player);
    }
    
    private void fireSingleShot(Player player) {
        // Анимация энергетического луча
        plugin.getAnimationManager().playBeamAnimation(
            player.getEyeLocation(),
            player.getEyeLocation().add(player.getLocation().getDirection().multiply(20)),
            Particle.ELECTRIC_SPARK,
            20
        );
        
        // Урон ближайшему врагу
        player.getWorld().getNearbyEntities(player.getLocation(), 20, 20, 20).stream()
            .filter(entity -> entity instanceof org.bukkit.entity.LivingEntity)
            .filter(entity -> !entity.equals(player))
            .findFirst()
            .ifPresent(entity -> {
                ((org.bukkit.entity.LivingEntity) entity).damage(25, player);
                
                // Эффект попадания
                plugin.getAnimationManager().playEnergyExplosion(
                    entity.getLocation(), 2, Particle.ELECTRIC_SPARK);
            });
        
        MessageUtil.sendMessage(player, "&cОдиночный выстрел! Урон: 25");
    }
    
    private void fireBurstShot(Player player) {
        // Серия из 3 выстрелов
        for (int i = 0; i < 3; i++) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getAnimationManager().playBeamAnimation(
                    player.getEyeLocation(),
                    player.getEyeLocation().add(player.getLocation().getDirection().multiply(15)),
                    Particle.FLAME,
                    15
                );
                
                player.getWorld().getNearbyEntities(player.getLocation(), 15, 15, 15).stream()
                    .filter(entity -> entity instanceof org.bukkit.entity.LivingEntity)
                    .filter(entity -> !entity.equals(player))
                    .findFirst()
                    .ifPresent(entity -> {
                        ((org.bukkit.entity.LivingEntity) entity).damage(15, player);
                    });
            }, i * 5L);
        }
        
        MessageUtil.sendMessage(player, "&cОчередь! 3 выстрела по 15 урона");
    }
    
    private void firePiercingShot(Player player) {
        // Пробивающий выстрел с эффектом молнии
        plugin.getAnimationManager().playLightningAnimation(
            player.getEyeLocation(),
            player.getEyeLocation().add(player.getLocation().getDirection().multiply(25)),
            10
        );
        
        // Урон всем врагам в радиусе
        player.getWorld().getNearbyEntities(player.getLocation(), 25, 25, 25).stream()
            .filter(entity -> entity instanceof org.bukkit.entity.LivingEntity)
            .filter(entity -> !entity.equals(player))
            .limit(3)
            .forEach(entity -> {
                ((org.bukkit.entity.LivingEntity) entity).damage(20, player);
                
                // Эффект попадания для каждой цели
                plugin.getAnimationManager().playEnergyExplosion(
                    entity.getLocation(), 1, Particle.END_ROD);
            });
        
        MessageUtil.sendMessage(player, "&cПробивающий выстрел! Урон: 20 (несколько целей)");
    }
    
    private void handleUraniumTablet(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        
        if (player.isSneaking()) {
            // Быстрый статус
            showQuickStatus(player);
        } else {
            // Главное меню планшета
            openTabletMenu(player);
        }
        
        setLastUse(player);
    }
    
    private void showQuickStatus(Player player) {
        int radiation = plugin.getRadiationManager().getPlayerRadiation(player);
        
        // Голографический дисплей статуса
        plugin.getVisualEffectsManager().createHologram(
            player.getLocation().add(0, 3, 0),
            "§6☢ Радиация: " + radiation + " рад",
            100
        );
        
        MessageUtil.sendMessage(player, "&6=== Быстрый статус ===");
        MessageUtil.sendMessage(player, "&7Радиация: &f" + radiation + " рад");
        MessageUtil.sendMessage(player, "&7Энергия планшета: &f100%");
        
        // Визуальные эффекты сканирования
        plugin.getVisualEffectsManager().playTabletEffect(player, "scan");
        plugin.getAnimationManager().playPulseAnimation(
            player.getLocation(), Particle.ENCHANT, 3, 40);
    }
    
    private void openTabletMenu(Player player) {
        // Открытие GUI планшета с эффектами
        plugin.getVisualEffectsManager().playTabletEffect(player, "open");
        plugin.getCompactGUIManager().openMainMenu(player);
    }
    
    private void handleGeigerCounter(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        
        int radiation = plugin.getRadiationManager().getPlayerRadiation(player);
        var level = plugin.getRadiationManager().getRadiationLevel(radiation);
        
        MessageUtil.sendMessage(player, "&e=== Счетчик Гейгера ===");
        MessageUtil.sendMessage(player, "&7Радиация: &f" + radiation + " рад");
        MessageUtil.sendMessage(player, "&7Уровень: &f" + level.name());
        
        // Звук счетчика с визуальными эффектами
        float pitch = Math.min(2.0f, 0.5f + (radiation / 100.0f));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, pitch);
        
        // Визуальная индикация уровня радиации
        if (radiation > 0) {
            plugin.getAnimationManager().playPulseAnimation(
                player.getLocation(), Particle.DUST, radiation / 50, 60);
        }
        
        setLastUse(player);
    }
    
    private void updateRailgunLore(ItemStack item, String mode) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            java.util.List<String> lore = meta.getLore();
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    if (lore.get(i).contains("Режим:")) {
                        lore.set(i, "§8Режим: " + mode);
                        break;
                    }
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
    }
    
    private boolean hasRecentlyUsed(Player player) {
        long lastUseTime = lastUse.getOrDefault(player.getUniqueId(), 0L);
        return System.currentTimeMillis() - lastUseTime < 1000; // 1 секунда кулдаун
    }
    
    private void setLastUse(Player player) {
        lastUse.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
