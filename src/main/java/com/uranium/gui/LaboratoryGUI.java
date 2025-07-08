package com.uranium.gui;

import com.uranium.UraniumCraft;
import com.uranium.managers.LaboratoryManager;
import com.uranium.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class LaboratoryGUI {
    
    private final UraniumCraft plugin;
    private final Player player;
    private final LaboratoryManager.LaboratoryData data;
    
    public LaboratoryGUI(UraniumCraft plugin, Player player, LaboratoryManager.LaboratoryData data) {
        this.plugin = plugin;
        this.player = player;
        this.data = data;
    }
    
    public void open() {
        Inventory inv = Bukkit.createInventory(null, 45, "§9⚗ Лаборатория ⚗");
        
        // Статус лаборатории
        ItemStack status = new ItemStack(Material.ENCHANTING_TABLE);
        ItemMeta statusMeta = status.getItemMeta();
        if (statusMeta != null) {
            statusMeta.setDisplayName("§9Статус лаборатории");
            statusMeta.setLore(Arrays.asList(
                "§7Уровень: " + data.getLevel(),
                "§7Энергия: " + data.getEnergy() + "/" + data.getMaxEnergy(),
                "§7Активное исследование: " + (data.hasActiveResearch() ? "Да" : "Нет")
            ));
            status.setItemMeta(statusMeta);
        }
        inv.setItem(22, status);
        
        // Доступные исследования
        int slot = 10;
        for (LaboratoryManager.ResearchType research : LaboratoryManager.ResearchType.values()) {
            if (slot >= 35) break;
            
            ItemStack researchItem = new ItemStack(Material.BOOK);
            ItemMeta researchMeta = researchItem.getItemMeta();
            if (researchMeta != null) {
                researchMeta.setDisplayName("§e" + research.getDisplayName());
                researchMeta.setLore(Arrays.asList(
                    "§7Время: " + (research.getDuration() / 1000 / 60) + " минут",
                    "§7Энергия: " + research.getEnergyCost(),
                    "§8ЛКМ - начать исследование"
                ));
                researchItem.setItemMeta(researchMeta);
            }
            inv.setItem(slot, researchItem);
            
            slot++;
            if (slot % 9 == 8) slot += 2;
        }
        
        player.openInventory(inv);
    }
}
