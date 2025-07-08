package com.uranium.managers;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Компактный менеджер GUI - объединяет все интерфейсы в одном классе
 */
public class CompactGUIManager {
    
    private final UraniumCraft plugin;
    
    public CompactGUIManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    // === ГЛАВНОЕ МЕНЮ ===
    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§6⚛ UraniumCraft ⚛");
        
        // Статус радиации
        int radiation = plugin.getRadiationManager().getPlayerRadiation(player);
        var level = plugin.getRadiationManager().getRadiationLevel(radiation);
        inv.setItem(10, createItem(Material.REDSTONE, "§c☢ Радиация: " + radiation + " рад", 
            Arrays.asList("§7Уровень: " + level.name(), "§8ЛКМ - детали")));
        
        // Структуры
        inv.setItem(12, createItem(Material.BEACON, "§9⚡ Структуры", 
            Arrays.asList("§7Управление постройками", "§8ЛКМ - открыть")));
        
        // Предметы
        inv.setItem(14, createItem(Material.CHEST, "§d📚 Предметы", 
            Arrays.asList("§7Каталог всех предметов", "§8ЛКМ - открыть")));
        
        // Достижения
        int achievements = plugin.getAchievementManager().getPlayerAchievements(player).size();
        inv.setItem(16, createItem(Material.DIAMOND, "§a🏆 Достижения", 
            Arrays.asList("§7Получено: " + achievements, "§8ЛКМ - открыть")));
        
        // Руководство
        inv.setItem(28, createItem(Material.BOOK, "§e📖 Руководство", 
            Arrays.asList("§7Изучите основы плагина", "§8ЛКМ - открыть")));
        
        // Админ панель
        if (player.hasPermission("uraniumcraft.admin")) {
            inv.setItem(34, createItem(Material.BARRIER, "§c🚨 Админ панель", 
                Arrays.asList("§7Административные функции", "§8ЛКМ - открыть")));
        }
        
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1.5f);
    }
    
    // === КАТАЛОГ ПРЕДМЕТОВ ===
    public void openItemCatalog(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§d📚 Каталог предметов");
        
        // Урановые материалы
        inv.setItem(10, plugin.getUraniumItems().getUraniumDust());
        inv.setItem(11, plugin.getUraniumItems().getUraniumIngot());
        inv.setItem(12, plugin.getUraniumItems().getUraniumBlock());
        inv.setItem(13, plugin.getUraniumItems().getUraniumCapsule());
        
        // Защитное снаряжение
        inv.setItem(19, plugin.getUraniumItems().getChemProtectionHelmet());
        inv.setItem(20, plugin.getUraniumItems().getChemProtectionChestplate());
        inv.setItem(21, plugin.getUraniumItems().getChemProtectionLeggings());
        inv.setItem(22, plugin.getUraniumItems().getChemProtectionBoots());
        
        // Силовая броня
        inv.setItem(28, plugin.getUraniumItems().getPowerArmorHelmet());
        inv.setItem(29, plugin.getUraniumItems().getPowerArmorChestplate());
        inv.setItem(30, plugin.getUraniumItems().getPowerArmorLeggings());
        inv.setItem(31, plugin.getUraniumItems().getPowerArmorBoots());
        
        // Устройства
        inv.setItem(37, plugin.getUraniumItems().getRailgun());
        inv.setItem(38, plugin.getUraniumItems().getUraniumTablet());
        inv.setItem(39, plugin.getUraniumItems().getGeigerCounter());
        
        // Строительные блоки
        inv.setItem(46, plugin.getUraniumItems().getCentrifugeCore());
        inv.setItem(47, plugin.getUraniumItems().getLaboratoryTerminal());
        inv.setItem(48, plugin.getUraniumItems().getTeleportCore());
        
        // Кнопка назад
        inv.setItem(49, createItem(Material.ARROW, "§7🔙 Назад", Arrays.asList("§8Вернуться в главное меню")));
        
        player.openInventory(inv);
    }
    
    // === ДОСТИЖЕНИЯ ===
    public void openAchievements(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§a🏆 Достижения");
        
        int slot = 10;
        for (AchievementManager.Achievement achievement : AchievementManager.Achievement.values()) {
            if (slot >= 35) break;
            
            boolean unlocked = plugin.getAchievementManager().hasAchievement(player, achievement);
            Material material = unlocked ? Material.DIAMOND : Material.COAL;
            String name = (unlocked ? "§a✓ " : "§7✗ ") + achievement.getDisplayName();
            
            inv.setItem(slot, createItem(material, name, Arrays.asList(
                "§7" + achievement.getDescription(),
                unlocked ? "§a✓ Получено!" : "§7Не получено"
            )));
            
            slot++;
            if (slot % 9 == 8) slot += 2;
        }
        
        inv.setItem(40, createItem(Material.ARROW, "§7🔙 Назад", Arrays.asList("§8Вернуться в главное меню")));
        player.openInventory(inv);
    }
    
    // === РУКОВОДСТВО ===
    public void openGuide(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§e📖 Руководство");
        
        inv.setItem(10, createItem(Material.GUNPOWDER, "§6Урановые материалы", Arrays.asList(
            "§7Урановая пыль - основа всего",
            "§7Получается только из центрифуги",
            "§c⚠ Радиоактивно!"
        )));
        
        inv.setItem(12, createItem(Material.FURNACE, "§6Центрифуга", Arrays.asList(
            "§7Структура 3x3:",
            "§8I C I",
            "§8C B C",
            "§8I C I",
            "§7B=Блок центрифуги, C=Котел, I=Железо"
        )));
        
        inv.setItem(14, createItem(Material.ENCHANTING_TABLE, "§9Лаборатория", Arrays.asList(
            "§7Простая постройка:",
            "§7Поставьте стол зачарований",
            "§7Проводите исследования"
        )));
        
        inv.setItem(16, createItem(Material.END_PORTAL_FRAME, "§dТелепорт", Arrays.asList(
            "§7Структура 3x3:",
            "§8I R I",
            "§8R T R",
            "§8I R I",
            "§7T=Блок телепорта, R=Редстоун, I=Железо"
        )));
        
        inv.setItem(28, createItem(Material.LEATHER_HELMET, "§aЗащита", Arrays.asList(
            "§7Химзащита - базовая защита",
            "§7Силовая броня - продвинутая",
            "§7Каждый элемент дает 25% защиты"
        )));
        
        inv.setItem(30, createItem(Material.REDSTONE, "§cБезопасность", Arrays.asList(
            "§c⚠ Уран радиоактивен!",
            "§7Всегда используйте защиту",
            "§7Храните в урановых капсулах"
        )));
        
        inv.setItem(40, createItem(Material.ARROW, "§7🔙 Назад", Arrays.asList("§8Вернуться в главное меню")));
        player.openInventory(inv);
    }
    
    // === АДМИН ПАНЕЛЬ ===
    public void openAdminPanel(Player player) {
        if (!player.hasPermission("uraniumcraft.admin")) {
            MessageUtil.sendMessage(player, "&cУ вас нет прав!");
            return;
        }
        
        Inventory inv = Bukkit.createInventory(null, 45, "§c🚨 Админ панель");
        
        inv.setItem(10, createItem(Material.REDSTONE, "§cОчистить радиацию", Arrays.asList(
            "§7Очистить радиацию всех игроков",
            "§8ЛКМ - выполнить"
        )));
        
        inv.setItem(12, createItem(Material.CHEST, "§6Выдать предметы", Arrays.asList(
            "§7Выдача предметов плагина",
            "§8ЛКМ - открыть меню"
        )));
        
        inv.setItem(14, createItem(Material.BOOK, "§bСтатистика", Arrays.asList(
            "§7Игроков онлайн: " + plugin.getServer().getOnlinePlayers().size(),
            "§7Общая радиация: " + getTotalRadiation() + " рад"
        )));
        
        inv.setItem(16, createItem(Material.BARRIER, "§cПерезагрузить", Arrays.asList(
            "§7Перезагрузить плагин",
            "§8ЛКМ - выполнить"
        )));
        
        inv.setItem(40, createItem(Material.ARROW, "§7🔙 Назад", Arrays.asList("§8Вернуться в главное меню")));
        player.openInventory(inv);
    }
    
    // === ОБРАБОТКА КЛИКОВ ===
    public void handleClick(Player player, Inventory inv, int slot) {
        String title = player.getOpenInventory().getTitle();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        
        if (title.contains("UraniumCraft")) {
            handleMainMenuClick(player, slot);
        } else if (title.contains("Каталог")) {
            if (slot == 49) openMainMenu(player);
        } else if (title.contains("Достижения")) {
            if (slot == 40) openMainMenu(player);
        } else if (title.contains("Руководство")) {
            if (slot == 40) openMainMenu(player);
        } else if (title.contains("Админ")) {
            handleAdminClick(player, slot);
        }
    }
    
    private void handleMainMenuClick(Player player, int slot) {
        switch (slot) {
            case 10:
                showRadiationDetails(player);
                break;
            case 12:
                MessageUtil.sendMessage(player, "&7Функция структур в разработке!");
                break;
            case 14:
                openItemCatalog(player);
                break;
            case 16:
                openAchievements(player);
                break;
            case 28:
                openGuide(player);
                break;
            case 34:
                openAdminPanel(player);
                break;
        }
    }
    
    private void handleAdminClick(Player player, int slot) {
        switch (slot) {
            case 10:
                // Очистка радиации всех игроков
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    plugin.getRadiationManager().clearPlayerRadiation(p);
                }
                MessageUtil.sendMessage(player, "&aРадиация всех игроков очищена!");
                break;
            case 12:
                MessageUtil.sendMessage(player, "&7Используйте команду /uraniumadmin give <игрок> <предмет>");
                break;
            case 14:
                MessageUtil.sendMessage(player, "&bСтатистика сервера обновлена!");
                break;
            case 16:
                plugin.reload();
                MessageUtil.sendMessage(player, "&aПлагин перезагружен!");
                break;
            case 40:
                openMainMenu(player);
                break;
        }
    }
    
    private void showRadiationDetails(Player player) {
        int radiation = plugin.getRadiationManager().getPlayerRadiation(player);
        var level = plugin.getRadiationManager().getRadiationLevel(radiation);
        
        MessageUtil.sendMessage(player, "&6=== ☢ СТАТУС РАДИАЦИИ ☢ ===");
        MessageUtil.sendMessage(player, "&7Уровень: &f" + radiation + " рад");
        MessageUtil.sendMessage(player, "&7Опасность: &f" + level.name());
        
        if (radiation > 0) {
            MessageUtil.sendMessage(player, "&c⚠ Используйте защиту!");
        } else {
            MessageUtil.sendMessage(player, "&a✓ Безопасный уровень");
        }
    }
    
    private int getTotalRadiation() {
        int total = 0;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            total += plugin.getRadiationManager().getPlayerRadiation(p);
        }
        return total;
    }
    
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
