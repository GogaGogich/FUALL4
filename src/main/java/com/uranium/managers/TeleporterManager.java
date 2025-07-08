package com.uranium.managers;

import com.uranium.UraniumCraft;
import com.uranium.gui.TeleporterGUI;
import com.uranium.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeleporterManager {
    
    private final UraniumCraft plugin;
    private final Map<Location, TeleporterData> teleporters = new ConcurrentHashMap<>();
    private final Map<String, Location> namedTeleporters = new ConcurrentHashMap<>();
    
    public TeleporterManager(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    public boolean createTeleporter(Location location, Player player, String name) {
        if (!isValidTeleporterStructure(location)) {
            MessageUtil.sendMessage(player, "&cНеправильная структура телепорта!");
            return false;
        }
        
        if (namedTeleporters.containsKey(name)) {
            MessageUtil.sendMessage(player, "&cТелепорт с таким названием уже существует!");
            return false;
        }
        
        TeleporterData data = new TeleporterData(location, player.getUniqueId(), name);
        teleporters.put(location, data);
        namedTeleporters.put(name, location);
        
        MessageUtil.sendMessage(player, "&aТелепорт '" + name + "' успешно создан!");
        return true;
    }
    
    public boolean isValidTeleporterStructure(Location center) {
        // Проверка структуры телепорта 3x3x1
        // I R I
        // R T R  (T = Блок телепорта, R = Редстоун блок, I = Железный блок)
        // I R I
        
        // Центральный блок - блок телепорта
        if (!isTeleportCore(center.getBlock().getType())) {
            return false;
        }
        
        // Проверка редстоун блоков по бокам
        Location[] redstoneLocations = {
            center.clone().add(0, 0, 1),  // Север
            center.clone().add(1, 0, 0),  // Восток
            center.clone().add(0, 0, -1), // Юг
            center.clone().add(-1, 0, 0)  // Запад
        };
        
        for (Location loc : redstoneLocations) {
            if (loc.getBlock().getType() != Material.REDSTONE_BLOCK) {
                return false;
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
    
    private boolean isTeleportCore(Material material) {
        // Проверяем, является ли блок блоком телепорта (кастомный предмет)
        return material == Material.END_PORTAL_FRAME; // Временно используем как блок телепорта
    }
    
    public void openTeleporterGUI(Player player, Location location) {
        TeleporterData data = teleporters.get(location);
        if (data == null) {
            MessageUtil.sendMessage(player, "&cТелепорт не найден!");
            return;
        }
        
        new TeleporterGUI(plugin, player, data).open();
    }
    
    public boolean teleportPlayer(Player player, String targetName) {
        Location targetLocation = namedTeleporters.get(targetName);
        if (targetLocation == null) {
            MessageUtil.sendMessage(player, "&cТелепорт '" + targetName + "' не найден!");
            return false;
        }
        
        TeleporterData targetData = teleporters.get(targetLocation);
        if (targetData == null) {
            MessageUtil.sendMessage(player, "&cТелепорт поврежден!");
            return false;
        }
        
        // Проверка энергии
        double distance = player.getLocation().distance(targetLocation);
        int energyCost = (int) (distance * 10);
        
        if (targetData.getEnergy() < energyCost) {
            MessageUtil.sendMessage(player, "&cНедостаточно энергии для телепортации!");
            return false;
        }
        
        // Проверка доступа
        if (!targetData.isPublic() && !targetData.hasAccess(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "&cУ вас нет доступа к этому телепорту!");
            return false;
        }
        
        // Телепортация
        performTeleportation(player, targetLocation, targetData, energyCost);
        return true;
    }
    
    private void performTeleportation(Player player, Location targetLocation, TeleporterData data, int energyCost) {
        // Эффекты телепортации
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 50);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        // Телепортация
        Location teleportLoc = targetLocation.clone().add(0.5, 1, 0.5);
        player.teleport(teleportLoc);
        
        // Эффекты прибытия
        player.getWorld().spawnParticle(Particle.PORTAL, teleportLoc, 50);
        player.getWorld().playSound(teleportLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        // Потребление энергии
        data.removeEnergy(energyCost);
        
        MessageUtil.sendMessage(player, "&aТелепортация успешна! Потрачено энергии: " + energyCost);
    }
    
    public void chargeTeleporter(Player player, Location location, int amount) {
        TeleporterData data = teleporters.get(location);
        if (data == null) {
            MessageUtil.sendMessage(player, "&cТелепорт не найден!");
            return;
        }
        
        if (!data.isOwner(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "&cТолько владелец может заряжать телепорт!");
            return;
        }
        
        // Проверка наличия редстоуна
        if (!player.getInventory().contains(Material.REDSTONE, amount)) {
            MessageUtil.sendMessage(player, "&cНедостаточно редстоуна для зарядки!");
            return;
        }
        
        // Зарядка
        player.getInventory().removeItem(new org.bukkit.inventory.ItemStack(Material.REDSTONE, amount));
        data.addEnergy(amount * 10);
        
        MessageUtil.sendMessage(player, "&aТелепорт заряжен на " + (amount * 10) + " единиц энергии!");
    }
    
    public List<TeleporterData> getAvailableTeleporters(Player player) {
        List<TeleporterData> available = new ArrayList<>();
        
        for (TeleporterData data : teleporters.values()) {
            if (data.isPublic() || data.hasAccess(player.getUniqueId())) {
                available.add(data);
            }
        }
        
        return available;
    }
    
    public TeleporterData getTeleporterData(Location location) {
        return teleporters.get(location);
    }
    
    public boolean isTeleporter(Location location) {
        return teleporters.containsKey(location);
    }
    
    public void removeTeleporter(Location location) {
        TeleporterData data = teleporters.remove(location);
        if (data != null) {
            namedTeleporters.remove(data.getName());
        }
    }
    
    public void saveAllData() {
        // Сохранение данных всех телепортов
    }
    
    public void reload() {
        // Перезагрузка настроек телепортов
    }
    
    public static class TeleporterData {
        private final Location location;
        private final UUID ownerId;
        private final String name;
        private int energy;
        private int maxEnergy;
        private boolean isPublic;
        private Set<UUID> authorizedUsers;
        
        public TeleporterData(Location location, UUID ownerId, String name) {
            this.location = location;
            this.ownerId = ownerId;
            this.name = name;
            this.energy = 1000;
            this.maxEnergy = 5000;
            this.isPublic = false;
            this.authorizedUsers = new HashSet<>();
        }
        
        public void addEnergy(int amount) {
            this.energy = Math.min(maxEnergy, this.energy + amount);
        }
        
        public void removeEnergy(int amount) {
            this.energy = Math.max(0, this.energy - amount);
        }
        
        public void addAuthorizedUser(UUID userId) {
            authorizedUsers.add(userId);
        }
        
        public void removeAuthorizedUser(UUID userId) {
            authorizedUsers.remove(userId);
        }
        
        public boolean hasAccess(UUID userId) {
            return isOwner(userId) || authorizedUsers.contains(userId);
        }
        
        public boolean isOwner(UUID userId) {
            return ownerId.equals(userId);
        }
        
        // Геттеры и сеттеры
        public Location getLocation() { return location; }
        public UUID getOwnerId() { return ownerId; }
        public String getName() { return name; }
        public int getEnergy() { return energy; }
        public int getMaxEnergy() { return maxEnergy; }
        public boolean isPublic() { return isPublic; }
        public Set<UUID> getAuthorizedUsers() { return authorizedUsers; }
        
        public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
        public void setMaxEnergy(int maxEnergy) { this.maxEnergy = maxEnergy; }
    }
}
