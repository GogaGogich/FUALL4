package com.uranium.managers;

import com.uranium.UraniumCraft;
import com.uranium.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class UraniumItems {
    
    private final UraniumCraft plugin;
    
    public UraniumItems(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    // Урановые материалы
    public ItemStack getUraniumDust() {
        return new ItemBuilder(Material.GUNPOWDER)
                .setName("§6Урановая пыль")
                .setLore(Arrays.asList(
                    "§7Радиоактивная пыль",
                    "§c☢ Радиация: 5 рад/шт",
                    "§8Получается только из центрифуги",
                    "§8Используется для создания слитков",
                    "§c⚠ ТРЕБУЕТ АВТОРИЗАЦИЮ ДЛЯ ИСПОЛЬЗОВАНИЯ"
                ))
                .addEnchantment(Enchantment.SILK_TOUCH, 1)
                .hideEnchants()
                .build();
    }
    
    public ItemStack getUraniumIngot() {
        return new ItemBuilder(Material.IRON_INGOT)
                .setName("§6Урановый слиток")
                .setLore(Arrays.asList(
                    "§7Обработанный уран",
                    "§c☢ Радиация: 15 рад/шт",
                    "§8Основной материал для",
                    "§8создания оборудования"
                ))
                .addEnchantment(Enchantment.SILK_TOUCH, 1)
                .hideEnchants()
                .build();
    }
    
    public ItemStack getUraniumBlock() {
        return new ItemBuilder(Material.IRON_BLOCK)
                .setName("§6Урановый блок")
                .setLore(Arrays.asList(
                    "§7Блок из урана",
                    "§c☢ Радиация: 50 рад/шт",
                    "§8Используется для строительства",
                    "§8и хранения урана"
                ))
                .addEnchantment(Enchantment.SILK_TOUCH, 1)
                .hideEnchants()
                .build();
    }
    
    // Защитное снаряжение
    public ItemStack getChemProtectionHelmet() {
        return new ItemBuilder(Material.LEATHER_HELMET)
                .setName("§aШлем химзащиты")
                .setLore(Arrays.asList(
                    "§7Защищает от радиации",
                    "§a✓ Защита: 25%",
                    "§8Часть комплекта химзащиты",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.PROTECTION, 2)
                .build();
    }
    
    public ItemStack getChemProtectionChestplate() {
        return new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .setName("§aКостюм химзащиты")
                .setLore(Arrays.asList(
                    "§7Защищает от радиации",
                    "§a✓ Защита: 25%",
                    "§8Часть комплекта химзащиты",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.PROTECTION, 2)
                .build();
    }
    
    public ItemStack getChemProtectionLeggings() {
        return new ItemBuilder(Material.LEATHER_LEGGINGS)
                .setName("§aШтаны химзащиты")
                .setLore(Arrays.asList(
                    "§7Защищает от радиации",
                    "§a✓ Защита: 25%",
                    "§8Часть комплекта химзащиты",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.PROTECTION, 2)
                .build();
    }
    
    public ItemStack getChemProtectionBoots() {
        return new ItemBuilder(Material.LEATHER_BOOTS)
                .setName("§aБотинки химзащиты")
                .setLore(Arrays.asList(
                    "§7Защищает от радиации",
                    "§a✓ Защита: 25%",
                    "§8Часть комплекта химзащиты",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.PROTECTION, 2)
                .build();
    }
    
    // Силовая броня
    public ItemStack getPowerArmorHelmet() {
        return new ItemBuilder(Material.DIAMOND_HELMET)
                .setName("§bШлем силовой брони")
                .setLore(Arrays.asList(
                    "§7Продвинутая защита",
                    "§a✓ Защита от радиации: 25%",
                    "§b⚡ Энергия: 1000/1000",
                    "§8Режим: Standard",
                    "§8Shift+ПКМ - смена режима",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.PROTECTION, 4)
                .build();
    }
    
    public ItemStack getPowerArmorChestplate() {
        return new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .setName("§bКорпус силовой брони")
                .setLore(Arrays.asList(
                    "§7Продвинутая защита",
                    "§a✓ Защита от радиации: 25%",
                    "§b⚡ Энергия: 1000/1000",
                    "§8Режим: Standard",
                    "§8Shift+ПКМ - смена режима",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.PROTECTION, 4)
                .build();
    }
    
    public ItemStack getPowerArmorLeggings() {
        return new ItemBuilder(Material.DIAMOND_LEGGINGS)
                .setName("§bПоножи силовой брони")
                .setLore(Arrays.asList(
                    "§7Продвинутая защита",
                    "§a✓ Защита от радиации: 25%",
                    "§b⚡ Энергия: 1000/1000",
                    "§8Режим: Standard",
                    "§8Shift+ПКМ - смена режима",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.PROTECTION, 4)
                .build();
    }
    
    public ItemStack getPowerArmorBoots() {
        return new ItemBuilder(Material.DIAMOND_BOOTS)
                .setName("§bСапоги силовой брони")
                .setLore(Arrays.asList(
                    "§7Продвинутая защита",
                    "§a✓ Защита от радиации: 25%",
                    "§b⚡ Энергия: 1000/1000",
                    "§8Режим: Standard",
                    "§8Shift+ПКМ - смена режима",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.PROTECTION, 4)
                .build();
    }
    
    // Рельсотрон
    public ItemStack getRailgun() {
        return new ItemBuilder(Material.CROSSBOW)
                .setName("§cРельсотрон")
                .setLore(Arrays.asList(
                    "§7Энергетическое оружие",
                    "§b⚡ Энергия: 1000/1000",
                    "§8Режим: Single",
                    "§8ПКМ - выстрел",
                    "§8Shift+ПКМ - смена режима",
                    "§8Режимы: Single, Burst, Piercing",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.QUICK_CHARGE, 3)
                .build();
    }
    
    // Урановый планшет
    public ItemStack getUraniumTablet() {
        return new ItemBuilder(Material.CLOCK)
                .setName("§6Урановый планшет")
                .setLore(Arrays.asList(
                    "§7Универсальное устройство управления",
                    "§b⚡ Энергия: 2000/2000",
                    "§8ПКМ - главное меню",
                    "§8Shift+ПКМ - быстрый статус",
                    "§8Функции: управление, мониторинг, телепорт",
                    "§8Требует исследование в лаборатории"
                ))
                .addEnchantment(Enchantment.UNBREAKING, 10)
                .hideEnchants()
                .build();
    }
    
    // Урановая капсула
    public ItemStack getUraniumCapsule() {
        return new ItemBuilder(Material.BUCKET)
                .setName("§6Урановая капсула")
                .setLore(Arrays.asList(
                    "§7Контейнер для урановой пыли",
                    "§8Вместимость: 0/500",
                    "§a✓ Защита от радиации при хранении",
                    "§8ПКМ - открыть интерфейс"
                ))
                .addEnchantment(Enchantment.SILK_TOUCH, 1)
                .hideEnchants()
                .build();
    }
    
    // Счетчик Гейгера
    public ItemStack getGeigerCounter() {
        return new ItemBuilder(Material.COMPASS)
                .setName("§eСчетчик Гейгера")
                .setLore(Arrays.asList(
                    "§7Измеритель радиации",
                    "§8Показывает уровень радиации",
                    "§8в реальном времени",
                    "§8ПКМ - измерить радиацию"
                ))
                .addEnchantment(Enchantment.UNBREAKING, 3)
                .build();
    }
    
    // Специальные предметы для строительства
    public ItemStack getCentrifugeCore() {
        return new ItemBuilder(Material.FURNACE)
                .setName("§6Блок центрифуги")
                .setLore(Arrays.asList(
                    "§7Основа для центрифуги",
                    "§8Размещается в центре структуры 3x3",
                    "§8Требует котлы с водой по бокам",
                    "§8Железные блоки по углам",
                    "§8Крафтится по 2 штуки",
                    "§8Требует квантовый процессор"
                ))
                .addEnchantment(Enchantment.SILK_TOUCH, 1)
                .hideEnchants()
                .build();
    }
    
    public ItemStack getLaboratoryTerminal() {
        return new ItemBuilder(Material.ENCHANTING_TABLE)
                .setName("§9Терминал лаборатории")
                .setLore(Arrays.asList(
                    "§7Управление лабораторией",
                    "§8Уровень: 1",
                    "§8ПКМ - открыть интерфейс",
                    "§8Проводит исследования"
                ))
                .addEnchantment(Enchantment.SILK_TOUCH, 1)
                .hideEnchants()
                .build();
    }
    
    public ItemStack getTeleportCore() {
        return new ItemBuilder(Material.END_PORTAL_FRAME)
                .setName("§dБлок телепорта")
                .setLore(Arrays.asList(
                    "§7Основа телепорта",
                    "§8Размещается в центре структуры 3x3",
                    "§8Требует редстоун блоки по бокам",
                    "§8Железные блоки по углам",
                    "§8Крафтится по 2 штуки",
                    "§8Требует квантовый процессор"
                ))
                .addEnchantment(Enchantment.SILK_TOUCH, 1)
                .hideEnchants()
                .build();
    }
    
    // Квантовый процессор
    public ItemStack getQuantumProcessor() {
        return new ItemBuilder(Material.REDSTONE_BLOCK)
                .setName("§5Квантовый процессор")
                .setLore(Arrays.asList(
                    "§7Продвинутый компонент",
                    "§5§lОЧЕНЬ РЕДКИЙ ПРЕДМЕТ",
                    "§8Используется для крафта:",
                    "§8• Блока центрифуги (x2)",
                    "§8• Блока телепорта (x2)",
                    "§8• Терминала лаборатории",
                    "§c§lТРЕБУЕТ СЛОЖНЫЙ КРАФТ!",
                    "§8Рецепт: Алмазы + Изумруды + Редстоун блок"
                ))
                .addEnchantment(Enchantment.SILK_TOUCH, 1)
                .hideEnchants()
                .build();
    }
    
    // Утилиты для работы с предметами
    public boolean isUraniumItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String displayName = item.getItemMeta().getDisplayName();
        return displayName.contains("Урановый") || displayName.contains("Урановая");
    }
    
    public boolean isRadiationProtection(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String displayName = item.getItemMeta().getDisplayName();
        return displayName.contains("Химзащита") || displayName.contains("Силовая броня");
    }
    
    public boolean isRailgun(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getDisplayName().contains("Рельсотрон");
    }
    
    public boolean isUraniumTablet(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getDisplayName().contains("Урановый планшет");
    }
    
    public boolean isGeigerCounter(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getDisplayName().contains("Счетчик Гейгера");
    }
    
    public void giveItemToPlayer(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItem(player.getLocation(), item);
        }
    }
}
