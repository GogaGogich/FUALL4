package com.uranium.listeners;

import com.uranium.UraniumCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

public class CompactGUIListener implements Listener {
    
    private final UraniumCraft plugin;
    
    public CompactGUIListener(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Проверяем, что это наш GUI
        if (title.contains("UraniumCraft") || title.contains("Каталог") || 
            title.contains("Достижения") || title.contains("Руководство") || 
            title.contains("Админ") || title.contains("Рецепты") || 
            title.contains("Лаборатория")) {
            
            event.setCancelled(true);
            
            if (event.getCurrentItem() != null) {
                plugin.getCompactGUIManager().handleClick(player, event.getInventory(), event.getSlot());
            }
        }
    }
}
