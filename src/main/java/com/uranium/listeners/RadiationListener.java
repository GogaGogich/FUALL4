package com.uranium.listeners;

import com.uranium.UraniumCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class RadiationListener implements Listener {
    
    private final UraniumCraft plugin;
    
    public RadiationListener(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
            
            // Обновление радиации после изменения инвентаря
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getRadiationManager().updatePlayerRadiation(player);
            }, 1L);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
            
            // Обновление радиации после перетаскивания предметов
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getRadiationManager().updatePlayerRadiation(player);
            }, 1L);
        }
    }
    
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        // Обновление радиации при смене предмета в руке
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getRadiationManager().updatePlayerRadiation(event.getPlayer());
        }, 1L);
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // Обновление радиации при выбрасывании предмета
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getRadiationManager().updatePlayerRadiation(event.getPlayer());
        }, 1L);
    }
    
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        // Обновление радиации при поднятии предмета
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getRadiationManager().updatePlayerRadiation(event.getPlayer());
        }, 1L);
    }
}
