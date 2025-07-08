package com.uranium.listeners;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class LaboratoryListener implements Listener {
    
    private final UraniumCraft plugin;
    
    public LaboratoryListener(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.ENCHANTING_TABLE) {
            // Проверка на создание лаборатории
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (event.getPlayer().hasPermission("uraniumcraft.laboratory.create")) {
                    plugin.getLaboratoryManager().createLaboratory(event.getBlock().getLocation(), event.getPlayer());
                } else {
                    MessageUtil.sendMessage(event.getPlayer(), "&cУ вас нет прав для создания лаборатории!");
                }
            }, 1L);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
                // Проверка на клик по лаборатории
                if (plugin.getLaboratoryManager().isLaboratory(event.getClickedBlock().getLocation())) {
                    event.setCancelled(true);
                    plugin.getLaboratoryManager().openLaboratoryGUI(event.getPlayer(), event.getClickedBlock().getLocation());
                }
            }
        }
    }
}
