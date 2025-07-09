package com.uranium.listeners;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class AuthListener implements Listener {
    
    private final UraniumCraft plugin;
    
    public AuthListener(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthorized(player)) {
            MessageUtil.sendMessage(player, "&6=== ДОБРО ПОЖАЛОВАТЬ В URANIUMCRAFT ===");
            MessageUtil.sendMessage(player, "&cДля использования плагина требуется авторизация!");
            MessageUtil.sendMessage(player, "&eИспользуйте: &f/uraniumauth <код>");
            MessageUtil.sendMessage(player, "&8Обратитесь к администратору за кодом доступа");
            MessageUtil.sendMessage(player, "&7Без авторизации взаимодействие с плагином невозможно");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Проверяем, взаимодействует ли игрок с предметами плагина
        if (event.getItem() != null && isPluginItem(event.getItem())) {
            if (!plugin.getAuthManager().checkAuthAndNotify(player)) {
                event.setCancelled(true);
                return;
            }
        }
        
        // Проверяем взаимодействие со структурами плагина
        if (event.getClickedBlock() != null) {
            if (plugin.getCentrifugeManager().isCentrifuge(event.getClickedBlock().getLocation()) ||
                plugin.getLaboratoryManager().isLaboratory(event.getClickedBlock().getLocation()) ||
                plugin.getTeleporterManager().isTeleporter(event.getClickedBlock().getLocation())) {
                
                if (!plugin.getAuthManager().checkAuthAndNotify(player)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        // Проверяем, размещает ли игрок блоки плагина
        if (isPluginBlock(event.getItemInHand())) {
            if (!plugin.getAuthManager().checkAuthAndNotify(player)) {
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // Проверяем, ломает ли игрок структуры плагина
        if (plugin.getCentrifugeManager().isCentrifuge(event.getBlock().getLocation()) ||
            plugin.getLaboratoryManager().isLaboratory(event.getBlock().getLocation()) ||
            plugin.getTeleporterManager().isTeleporter(event.getBlock().getLocation())) {
            
            if (!plugin.getAuthManager().checkAuthAndNotify(player)) {
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        // Проверяем авторизацию для ВСЕХ предметов плагина
        if (event.getRecipe() != null && (isPluginItem(event.getRecipe().getResult()) || isPluginRecipe(event.getRecipe()))) {
            if (!plugin.getAuthManager().checkAuthAndNotify(player)) {
                event.setCancelled(true);
                return;
            }
        }
    }
    
    private boolean isPluginRecipe(org.bukkit.inventory.Recipe recipe) {
        if (recipe instanceof org.bukkit.inventory.ShapedRecipe) {
            org.bukkit.inventory.ShapedRecipe shaped = (org.bukkit.inventory.ShapedRecipe) recipe;
            return shaped.getKey().getNamespace().equals("uraniumcraft");
        }
        return false;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = player.getOpenInventory().getTitle();
        
        // Проверяем, открывает ли игрок GUI плагина
        if (title.contains("UraniumCraft") || title.contains("Центрифуга") || 
            title.contains("Лаборатория") || title.contains("Телепорт")) {
            
            if (!plugin.getAuthManager().checkAuthAndNotify(player)) {
                event.setCancelled(true);
                player.closeInventory();
                return;
            }
        }
    }
    
    private boolean isPluginItem(org.bukkit.inventory.ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        return displayName.contains("Урановый") || displayName.contains("Урановая") ||
               displayName.contains("Химзащита") || displayName.contains("Силовая броня") ||
               displayName.contains("Рельсотрон") || displayName.contains("Счетчик Гейгера") ||
               displayName.contains("Квантовый") || displayName.contains("Блок центрифуги") ||
               displayName.contains("Блок телепорта") || displayName.contains("Терминал лаборатории");
    }
    
    private boolean isPluginBlock(org.bukkit.inventory.ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        return displayName.contains("Блок центрифуги") ||
               displayName.contains("Блок телепорта") ||
               displayName.contains("Терминал лаборатории");
    }
}
