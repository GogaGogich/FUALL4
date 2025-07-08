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

public class CentrifugeManager {
    
    private final UraniumCraft plugin;
    private final Map<Location, CentrifugeData> centrifuges = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public CentrifugeManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    public boolean createCentrifuge(Location location, Player player) {
        if (!isValidCentrifugeStructure(location)) {
            MessageUtil.sendMessage(player, "&cНеправильная структура центрифуги!");
            return false;
        }
        
        CentrifugeData data = new CentrifugeData(location, player.getUniqueId());
        centrifuges.put(location, data);
        
        MessageUtil.sendMessage(player, "&aЦентрифуга успешно создана!");
        plugin.getPlayerStatsManager().addCentrifugeBuilt(player);
        
        return true;
    }
    
    public boolean isValidCentrifugeStructure(Location center) {
        // Проверка структуры центрифуги 3x3x1
        // I C I
        // C B C  (B = Блок центрифуги, C = Котел с водой, I = Железный блок)
        // I C I
        
        Block centerBlock = center.getBlock();
        if (!isCentrifugeCore(centerBlock.getType())) {
            return false;
        }
        
        // Проверка котлов с водой по бокам
        Location[] cauldronLocations = {
            center.clone().add(0, 0, 1),  // Север
            center.clone().add(1, 0, 0),  // Восток
            center.clone().add(0, 0, -1), // Юг
            center.clone().add(-1, 0, 0)  // Запад
        };
        
        for (Location loc : cauldronLocations) {
            Block block = loc.getBlock();
            if (block.getType() != Material.WATER_CAULDRON) {
                return false;
            }
            
            // Проверяем, что котел полный (уровень воды = 3)
            if (block.getBlockData() instanceof Levelled) {
                Levelled levelled = (Levelled) block.getBlockData();
                if (levelled.getLevel() != levelled.getMaximumLevel()) {
                    return false;
                }
            }
        }
        
        // Проверка железных блоков по углам
        Location[] ironLocations = {
            center.clone().add(1, 0, 1),   // Северо-восток
            center.clone().add(1, 0, -1),  // Юго-восток
            center.clone().add(-1, 0, -1), // Юго-запад
            center.clone().add(-1, 0, 1)   // Северо-запад
        };
        
        for (Location loc : ironLocations) {
            if (loc.getBlock().getType() != Material.IRON_BLOCK) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isCentrifugeCore(Material material) {
        // Проверяем, является ли блок блоком центрифуги (кастомный предмет)
        return material == Material.FURNACE; // Временно используем как блок центрифуги
    }
    
    public void openCentrifugeGUI(Player player, Location location) {
        CentrifugeData data = centrifuges.get(location);
        if (data == null) {
            MessageUtil.sendMessage(player, "&cЦентрифуга не найдена!");
            return;
        }
        
        new CentrifugeGUI(plugin, player, data).open();
    }
    
    public boolean startCentrifuge(Player player, Location location) {
        CentrifugeData data = centrifuges.get(location);
        if (data == null) {
            MessageUtil.sendMessage(player, "&cЦентрифуга не найдена!");
            return false;
        }
        
        if (data.isRunning()) {
            MessageUtil.sendMessage(player, "&cЦентрифуга уже работает!");
            return false;
        }
        
        if (!isValidCentrifugeStructure(location)) {
            MessageUtil.sendMessage(player, "&cСтруктура центрифуги повреждена! Проверьте котлы с водой.");
            return false;
        }
        
        // Запуск процесса (без требования материалов - уран можно получить только из центрифуги)
        data.startProcessing();
        
        MessageUtil.sendMessage(player, "&aЦентрифуга запущена! Время обработки: 5 минут");
        MessageUtil.sendMessage(player, "&eПо завершении вы получите от 1 до 5 урановой пыли");
        
        // Визуальные эффекты
        location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location.add(0.5, 1, 0.5), 20);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
        
        return true;
    }
    
    public boolean stopCentrifuge(Player player, Location location) {
        CentrifugeData data = centrifuges.get(location);
        if (data == null) {
            MessageUtil.sendMessage(player, "&cЦентрифуга не найдена!");
            return false;
        }
        
        if (!data.isRunning()) {
            MessageUtil.sendMessage(player, "&cЦентрифуга не работает!");
            return false;
        }
        
        data.stopProcessing();
        MessageUtil.sendMessage(player, "&eЦентрифуга остановлена!");
        
        return true;
    }
    
    public void updateAllCentrifuges() {
        for (Map.Entry<Location, CentrifugeData> entry : centrifuges.entrySet()) {
            Location location = entry.getKey();
            CentrifugeData data = entry.getValue();
            
            if (data.isRunning()) {
                updateCentrifuge(location, data);
            }
        }
    }
    
    private void updateCentrifuge(Location location, CentrifugeData data) {
        if (data.isProcessingComplete()) {
            completeCentrifugeProcessing(location, data);
        } else {
            // Визуальные эффекты работы
            location.getWorld().spawnParticle(Particle.SMOKE, location.add(0.5, 1, 0.5), 5);
            if (System.currentTimeMillis() % 10000 == 0) { // Каждые 10 секунд
                location.getWorld().playSound(location, Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5f, 1.0f);
            }
        }
    }
    
    private void completeCentrifugeProcessing(Location location, CentrifugeData data) {
        data.stopProcessing();
        
        // Получение результатов
        Player player = plugin.getServer().getPlayer(data.getOwnerId());
        if (player != null && player.isOnline()) {
            giveProcessingResults(player, location);
            MessageUtil.sendMessage(player, "&aЦентрифуга завершила обработку!");
            
            // Добавление радиации
            plugin.getRadiationManager().setPlayerRadiation(player, 
                plugin.getRadiationManager().getPlayerRadiation(player) + 10);
            
            // Статистика
            plugin.getPlayerStatsManager().addCentrifugeUsed(player);
        }
        
        // Визуальные эффекты завершения
        location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location.add(0.5, 1, 0.5), 30);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
    }
    
    private void giveProcessingResults(Player player, Location location) {
        // Случайное количество урановой пыли от 1 до 5
        int dustAmount = random.nextInt(5) + 1;
        
        ItemStack uraniumDust = plugin.getUraniumItems().getUraniumDust();
        uraniumDust.setAmount(dustAmount);
        
        // Выбрасываем предметы рядом с центрифугой
        location.getWorld().dropItem(location.add(0.5, 1, 0.5), uraniumDust);
        
        MessageUtil.sendMessage(player, "&aПолучено урановой пыли: " + dustAmount + " шт.");
    }
    
    public CentrifugeData getCentrifugeData(Location location) {
        return centrifuges.get(location);
    }
    
    public boolean isCentrifuge(Location location) {
        return centrifuges.containsKey(location);
    }
    
    public void removeCentrifuge(Location location) {
        centrifuges.remove(location);
    }
    
    public void saveAllData() {
        // Сохранение данных всех центрифуг
        // Реализация сохранения в файл
    }
    
    public void reload() {
        // Перезагрузка настроек центрифуг
    }
    
    public static class CentrifugeData {
        private final Location location;
        private final UUID ownerId;
        private boolean isRunning;
        private long startTime;
        private long processingDuration = 5 * 60 * 1000; // 5 минут
        
        public CentrifugeData(Location location, UUID ownerId) {
            this.location = location;
            this.ownerId = ownerId;
            this.isRunning = false;
        }
        
        public void startProcessing() {
            this.isRunning = true;
            this.startTime = System.currentTimeMillis();
        }
        
        public void stopProcessing() {
            this.isRunning = false;
        }
        
        public boolean isRunning() {
            return isRunning;
        }
        
        public boolean isProcessingComplete() {
            return isRunning && (System.currentTimeMillis() - startTime) >= processingDuration;
        }
        
        public long getRemainingTime() {
            if (!isRunning) return 0;
            return Math.max(0, processingDuration - (System.currentTimeMillis() - startTime));
        }
        
        public int getProgressPercentage() {
            if (!isRunning) return 0;
            long elapsed = System.currentTimeMillis() - startTime;
            return (int) ((elapsed * 100) / processingDuration);
        }
        
        public Location getLocation() {
            return location;
        }
        
        public UUID getOwnerId() {
            return ownerId;
        }
    }
}
