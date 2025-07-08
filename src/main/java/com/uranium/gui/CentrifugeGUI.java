package com.uranium.gui;

import com.uranium.UraniumCraft;
import com.uranium.managers.CentrifugeManager;
import com.uranium.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CentrifugeGUI {
    
    private final UraniumCraft plugin;
    private final Player player;
    private final CentrifugeManager.CentrifugeData data;
    
    public CentrifugeGUI(UraniumCraft plugin, Player player, CentrifugeManager.CentrifugeData data) {
        this.plugin = plugin;
        this.player = player;
        this.data = data;
    }
    
    public void open() {
        Inventory inv = Bukkit.createInventory(null, 27, "§6⚛ Центрифуга ⚛");
        
        // Статус центрифуги
        ItemStack status = new ItemStack(data.isRunning() ? Material.GREEN_WOOL : Material.RED_WOOL);
        ItemMeta statusMeta = status.getItemMeta();
        if (statusMeta != null) {
            statusMeta.setDisplayName(data.isRunning() ? "§aРаботает" : "§cОстановлена");
            statusMeta.setLore(Arrays.asList(
                "§7Прогресс: " + data.getProgressPercentage() + "%",
                "§7Оставшееся время: " + (data.getRemainingTime() / 1000) + " сек"
            ));
            status.setItemMeta(statusMeta);
        }
        inv.setItem(13, status);
        
        // Кнопка запуска/остановки
        ItemStack control = new ItemStack(data.isRunning() ? Material.BARRIER : Material.EMERALD);
        ItemMeta controlMeta = control.getItemMeta();
        if (controlMeta != null) {
            controlMeta.setDisplayName(data.isRunning() ? "§cОстановить" : "§aЗапустить");
            controlMeta.setLore(Arrays.asList(
                "§7Нажмите для " + (data.isRunning() ? "остановки" : "запуска"),
                "§7центрифуги"
            ));
            control.setItemMeta(controlMeta);
        }
        inv.setItem(22, control);
        
        player.openInventory(inv);
    }
}
