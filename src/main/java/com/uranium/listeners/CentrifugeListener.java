package com.uranium.listeners;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class CentrifugeListener implements Listener {
    
    private final UraniumCraft plugin;
    
    public CentrifugeListener(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.FURNACE) {
            // Проверка на создание центрифуги
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (plugin.getCentrifugeManager().isValidCentrifugeStructure(event.getBlock().getLocation())) {
                    if (event.getPlayer().hasPermission("uraniumcraft.centrifuge.create")) {
                        plugin.getCentrifugeManager().createCentrifuge(event.getBlock().getLocation(), event.getPlayer());
                    } else {
                        MessageUtil.sendMessage(event.getPlayer(), "&cУ вас нет прав для создания центрифуги!");
                    }
                }
            }, 1L);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.FURNACE) {
                // Проверка на клик по центрифуге
                if (plugin.getCentrifugeManager().isCentrifuge(event.getClickedBlock().getLocation())) {
                    event.setCancelled(true);
                    plugin.getCentrifugeManager().openCentrifugeGUI(event.getPlayer(), event.getClickedBlock().getLocation());
                }
            }
        }
    }
}
