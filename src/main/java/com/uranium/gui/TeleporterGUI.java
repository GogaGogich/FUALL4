package com.uranium.gui;

import com.uranium.UraniumCraft;
import com.uranium.managers.TeleporterManager;
import com.uranium.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class TeleporterGUI {
    
    private final UraniumCraft plugin;
    private final Player player;
    private final TeleporterManager.TeleporterData data;
    
    public TeleporterGUI(UraniumCraft plugin, Player player, TeleporterManager.TeleporterData data) {
        this.plugin = plugin;
        this.player = player;
        this.data = data;
    }
    
    public void open() {
        Inventory inv = Bukkit.createInventory(null, 45, "§d⚡ Телепорт ⚡");
        
        // Статус телепорта
        ItemStack status = new ItemStack(Material.END_PORTAL_FRAME);
        ItemMeta statusMeta = status.getItemMeta();
        if (statusMeta != null) {
            statusMeta.setDisplayName("§dСтатус телепорта");
            statusMeta.setLore(Arrays.asList(
                "§7Название: " + data.getName(),
                "§7Энергия: " + data.getEnergy() + "/" + data.getMaxEnergy(),
                "§7Доступ: " + (data.isPublic() ? "Публичный" : "Приватный"),
                "§7Владелец: " + (data.isOwner(player.getUniqueId()) ? "Вы" : "Другой игрок")
            ));
            status.setItemMeta(statusMeta);
        }
        inv.setItem(22, status);
        
        // Доступные телепорты
        List<TeleporterManager.TeleporterData> available = plugin.getTeleporterManager().getAvailableTeleporters(player);
        int slot = 10;
        for (TeleporterManager.TeleporterData teleporter : available) {
            if (slot >= 35) break;
            
            ItemStack teleportItem = new ItemStack(Material.ENDER_PEARL);
            ItemMeta teleportMeta = teleportItem.getItemMeta();
            if (teleportMeta != null) {
                teleportMeta.setDisplayName("§d" + teleporter.getName());
                teleportMeta.setLore(Arrays.asList(
                    "§7Энергия: " + teleporter.getEnergy(),
                    "§7Доступ: " + (teleporter.isPublic() ? "Публичный" : "Приватный"),
                    "§8ЛКМ - телепортироваться"
                ));
                teleportItem.setItemMeta(teleportMeta);
            }
            inv.setItem(slot, teleportItem);
            
            slot++;
            if (slot % 9 == 8) slot += 2;
        }
        
        player.openInventory(inv);
    }
}
