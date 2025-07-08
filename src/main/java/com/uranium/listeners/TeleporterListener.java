package com.uranium.listeners;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class TeleporterListener implements Listener {
    
    private final UraniumCraft plugin;
    
    public TeleporterListener(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.END_PORTAL_FRAME) {
            // Проверка на создание телепорта
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (plugin.getTeleporterManager().isValidTeleporterStructure(event.getBlock().getLocation())) {
                    // Запрос названия телепорта
                    MessageUtil.sendMessage(event.getPlayer(), "&aТелепорт создан! Используйте планшет для настройки.");
                    
                    // Создание телепорта с временным именем
                    String tempName = "teleport_" + System.currentTimeMillis();
                    plugin.getTeleporterManager().createTeleporter(event.getBlock().getLocation(), event.getPlayer(), tempName);
                }
            }, 1L);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
                // Проверка на клик по телепорту
                if (plugin.getTeleporterManager().isTeleporter(event.getClickedBlock().getLocation())) {
                    event.setCancelled(true);
                    plugin.getTeleporterManager().openTeleporterGUI(event.getPlayer(), event.getClickedBlock().getLocation());
                }
            }
        }
    }
}
