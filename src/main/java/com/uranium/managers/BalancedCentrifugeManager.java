package com.uranium.managers;

import com.uranium.UraniumCraft;
import com.uranium.gui.CentrifugeGUI;
import com.uranium.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BalancedCentrifugeManager extends CentrifugeManager {
    
    private final UraniumCraft plugin;
    private final Map<Location, BalancedCentrifugeData> centrifuges = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public BalancedCentrifugeManager(UraniumCraft plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    @Override
    public boolean createCentrifuge(Location location, Player player) {
        if (!isValidCentrifugeStructure(location)) {
            MessageUtil.sendMessage(player, "&cНеправильная структура центрифуги!");
            showStructureRequirements(player);
            return false;
        }
        
        // Проверяем, изучена ли технология центрифуг
        if (!hasResearchedCentrifugeTechnology(player)) {
            MessageUtil.sendMessage(player, "&cСначала изучите технологию центрифуг в лаборатории!");
            return false;
        }
        
        BalancedCentrifugeData data = new BalancedCentrifugeData(location, player.getUniqueId());
        centrifuges.put(location, data);
        
        MessageUtil.sendMessage(player, "&a§l✓ Центрифуга успешно создана!");
        MessageUtil.sendMessage(player, "&7Единственный способ получения урановой пыли");
        MessageUtil.sendMessage(player, "&8ПКМ по центрифуге для управления");
        
        plugin.getPlayerStatsManager().addCentrifugeBuilt(player);
        
        // Визуальные эффекты создания
        plugin.getAnimationManager().playVortexAnimation(location, 3, Particle.ENCHANT, 60);
        player.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
        
        return true;
    }
    
    private boolean hasResearchedCentrifugeTechnology(Player player) {
        // Проверяем, завершено ли исследование технологии центрифуг
        // Это должно быть реализовано в лаборатории
        return true; // Временно разрешаем всем
    }
    
    private void showStructureRequirements(Player player) {
        MessageUtil.sendMessage(player, "&6§l=== ТРЕБОВАНИЯ К ЦЕНТРИФУГЕ ===");
        MessageUtil.sendMessage(player, "&7Структура 3x3:");
        MessageUtil.sendMessage(player, "&eI C I");
        MessageUtil.sendMessage(player, "&eC B C");
        MessageUtil.sendMessage(player, "&eI C I");
        MessageUtil.sendMessage(player, "&7Где:");
        MessageUtil.sendMessage(player, "&8• B = Блок центрифуги (крафтится)");
        MessageUtil.sendMessage(player, "&8• C = Котел с водой (полный!)");
        MessageUtil.sendMessage(player, "&8• I = Железный блок");
        MessageUtil.sendMessage(player, "&c⚠ Котлы должны быть полностью наполнены водой!");
        MessageUtil.sendMessage(player, "&c⚠ Блок центрифуги крафтится с квантовым процессором!");
    }
    
    @Override
    public boolean startCentrifuge(Player player, Location location) {
        BalancedCentrifugeData data = centrifuges.get(location);
        if (data == null) {
            MessageUtil.sendMessage(player, "&cЦентрифуга не найдена!");
            return false;
        }
        
        if (data.isRunning()) {
            MessageUtil.sendMessage(player, "&cЦентрифуга уже работает!");
            MessageUtil.sendMessage(player, "&7Оставшееся время: " + (data.getRemainingTime() / 1000) + " секунд");
            return false;
        }
        
        if (!isValidCentrifugeStructure(location)) {
            MessageUtil.sendMessage(player, "&cСтруктура центрифуги повреждена!");
            MessageUtil.sendMessage(player, "&7Проверьте:");
            MessageUtil.sendMessage(player, "&8• Целостность структуры 3x3");
            MessageUtil.sendMessage(player, "&8• Котлы полностью наполнены водой");
            MessageUtil.sendMessage(player, "&8• Все блоки на своих местах");
            return false;
        }
        
        // Проверяем энергию (если есть система энергии)
        if (data.getEnergy() < 100) {
            MessageUtil.sendMessage(player, "&cНедостаточно энергии для запуска!");
            MessageUtil.sendMessage(player, "&7Требуется: 100 энергии");
            MessageUtil.sendMessage(player, "&7Текущая: " + data.getEnergy());
            return false;
        }
        
        // Запуск процесса
        data.startProcessing();
        data.consumeEnergy(100);
        
        MessageUtil.sendMessage(player, "&a§l✓ Центрифуга запущена!");
        MessageUtil.sendMessage(player, "&eВремя обработки: &f5 минут");
        MessageUtil.sendMessage(player, "&eОжидаемый результат: &f1-5 урановой пыли");
        MessageUtil.sendMessage(player, "&c⚠ Радиация: &f+10 рад после завершения");
        
        // Визуальные и звуковые эффекты запуска
        plugin.getVisualEffectsManager().startCentrifugeEffects(location);
        location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location.add(0.5, 1, 0.5), 30);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
        
        return true;
    }
    
    @Override
    public void updateAllCentrifuges() {
        for (Map.Entry<Location, BalancedCentrifugeData> entry : centrifuges.entrySet()) {
            Location location = entry.getKey();
            BalancedCentrifugeData data = entry.getValue();
            
            if (data.isRunning()) {
                updateCentrifuge(location, data);
            } else {
                // Восстановление энергии для неактивных центрифуг
                data.regenerateEnergy();
            }
        }
    }
    
    private void updateCentrifuge(Location location, BalancedCentrifugeData data) {
        if (data.isProcessingComplete()) {
            completeCentrifugeProcessing(location, data);
        } else {
            // Визуальные эффекты работы
            if (System.currentTimeMillis() % 5000 == 0) { // Каждые 5 секунд
                location.getWorld().spawnParticle(Particle.SMOKE, location.add(0.5, 1, 0.5), 8);
                location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location.add(0.5, 1.5, 0.5), 5);
                location.getWorld().playSound(location, Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.3f, 1.2f);
            }
        }
    }
    
    private void completeCentrifugeProcessing(Location location, BalancedCentrifugeData data) {
        data.stopProcessing();
        
        Player player = plugin.getServer().getPlayer(data.getOwnerId());
        if (player != null && player.isOnline()) {
            giveProcessingResults(player, location, data);
            
            // Добавление радиации
            int currentRadiation = plugin.getRadiationManager().getPlayerRadiation(player);
            plugin.getRadiationManager().setPlayerRadiation(player, currentRadiation + 10);
            
            MessageUtil.sendMessage(player, "&a§l✓ Центрифуга завершила обработку!");
            MessageUtil.sendMessage(player, "&c☢ Получено +10 рад радиации");
            MessageUtil.sendMessage(player, "&7Используйте защитное снаряжение!");
            
            // Статистика
            plugin.getPlayerStatsManager().addCentrifugeUsed(player);
            
            // Проверка достижений
            plugin.getAchievementManager().checkAchievements(player);
        }
        
        // Визуальные эффекты завершения
        location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location.add(0.5, 1, 0.5), 20);
        location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location.add(0.5, 2, 0.5), 15);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
        location.getWorld().playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
        
        // Остановка визуальных эффектов
        plugin.getVisualEffectsManager().stopEffects(location);
    }
    
    private void giveProcessingResults(Player player, Location location, BalancedCentrifugeData data) {
        // Базовое количество: 1-5 урановой пыли
        int baseAmount = random.nextInt(5) + 1;
        
        // Бонусы от уровня центрифуги (если есть система улучшений)
        int bonusAmount = data.getLevel() > 1 ? random.nextInt(data.getLevel()) : 0;
        
        int totalAmount = baseAmount + bonusAmount;
        
        // Создаем урановую пыль
        ItemStack uraniumDust = plugin.getUraniumItems().getUraniumDust();
        uraniumDust.setAmount(totalAmount);
        
        // Выбрасываем предметы рядом с центрифугой
        location.getWorld().dropItem(location.add(0.5, 1, 0.5), uraniumDust);
        MessageUtil.sendMessage(player, "&a§l✓ Получено: &f" + totalAmount + " урановой пыли");
        
        // Дополнительные редкие материалы (5% шанс)
        if (random.nextInt(100) < 5) {
            ItemStack bonus = new ItemStack(Material.REDSTONE, random.nextInt(3) + 1);
            location.getWorld().dropItem(location.add(0.5, 1, 0.5), bonus);
            MessageUtil.sendMessage(player, "&e§l✦ Бонус: &f" + bonus.getAmount() + " редстоуна");
        }
    }
    
    public static class BalancedCentrifugeData extends CentrifugeData {
        private int energy;
        private int maxEnergy;
        private int level;
        private long lastEnergyRegen;
        
        public BalancedCentrifugeData(Location location, UUID ownerId) {
            super(location, ownerId);
            this.energy = 500; // Начальная энергия
            this.maxEnergy = 1000;
            this.level = 1;
            this.lastEnergyRegen = System.currentTimeMillis();
        }
        
        public void consumeEnergy(int amount) {
            this.energy = Math.max(0, this.energy - amount);
        }
        
        public void regenerateEnergy() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEnergyRegen >= 30000) { // Каждые 30 секунд
                this.energy = Math.min(maxEnergy, this.energy + 50);
                this.lastEnergyRegen = currentTime;
            }
        }
        
        public int getEnergy() { return energy; }
        public int getMaxEnergy() { return maxEnergy; }
        public int getLevel() { return level; }
        
        public void upgradeLevel() {
            if (level < 5) {
                level++;
                maxEnergy += 200;
            }
        }
    }
}
