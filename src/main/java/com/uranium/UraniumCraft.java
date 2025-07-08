package com.uranium;

import com.uranium.commands.AdminUraniumCommand;
import com.uranium.commands.AuthCommand;
import com.uranium.commands.UraniumAdminCommand;
import com.uranium.listeners.*;
import com.uranium.managers.*;
import com.uranium.utils.ConfigManager;
import com.uranium.utils.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class UraniumCraft extends JavaPlugin {
    
    private static UraniumCraft instance;
    
    // Основные менеджеры
    private RadiationManager radiationManager;
    private UraniumItems uraniumItems;
    private BalancedCentrifugeManager centrifugeManager;
    private EnhancedLaboratoryManager laboratoryManager;
    private TeleporterManager teleporterManager;
    private ConfigManager configManager;
    private AchievementManager achievementManager;
    private PlayerStatsManager playerStatsManager;
    private AuthManager authManager;
    
    // Визуальные менеджеры
    private EnhancedVisualEffectsManager visualEffectsManager;
    private HologramManager hologramManager;
    private SoundManager soundManager;
    private AnimationManager animationManager;
    
    // Новые менеджеры
    private AdvancedCraftingManager craftingManager;
    private CompactGUIManager compactGUIManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        MessageUtil.log("&6§l=== ЗАПУСК URANIUMCRAFT ===");
        
        // Инициализация конфигурации
        configManager = new ConfigManager(this);
        MessageUtil.log("&a✓ Конфигурация загружена");
        
        // Инициализация основных менеджеров
        radiationManager = new RadiationManager(this);
        uraniumItems = new UraniumItems(this);
        centrifugeManager = new BalancedCentrifugeManager(this);
        laboratoryManager = new EnhancedLaboratoryManager(this);
        teleporterManager = new TeleporterManager(this);
        achievementManager = new AchievementManager(this);
        playerStatsManager = new PlayerStatsManager(this);
        authManager = new AuthManager(this);
        MessageUtil.log("&a✓ Основные менеджеры инициализированы");
        
        // Инициализация визуальных менеджеров
        visualEffectsManager = new EnhancedVisualEffectsManager(this);
        hologramManager = new HologramManager(this);
        soundManager = new SoundManager(this);
        animationManager = new AnimationManager(this);
        MessageUtil.log("&a✓ Визуальные и звуковые эффекты активированы");
        
        // Инициализация новых менеджеров
        craftingManager = new AdvancedCraftingManager(this);
        compactGUIManager = new CompactGUIManager(this);
        MessageUtil.log("&a✓ Продвинутая система крафтов и компактный GUI активированы");
        
        // Регистрация команд
        getCommand("uranium").setExecutor(new AdminUraniumCommand(this));
        getCommand("uraniumauth").setExecutor(new AuthCommand(this));
        getCommand("uraniumadmin").setExecutor(new UraniumAdminCommand(this));
        MessageUtil.log("&a✓ Команды зарегистрированы");
        
        // Регистрация слушателей событий
        registerListeners();
        MessageUtil.log("&a✓ Обработчики событий зарегистрированы");
        
        // Запуск периодических задач
        startPeriodicTasks();
        MessageUtil.log("&a✓ Периодические задачи запущены");
        
        MessageUtil.log("&a§l✓ UraniumCraft успешно загружен!");
        MessageUtil.log("&eВерсия: &f" + getDescription().getVersion());
        MessageUtil.log("&eАвторы: &f" + String.join(", ", getDescription().getAuthors()));
        MessageUtil.log("&6§l=== ПЛАГИН ГОТОВ К РАБОТЕ ===");
    }
    
    @Override
    public void onDisable() {
        MessageUtil.log("&6§l=== ОТКЛЮЧЕНИЕ URANIUMCRAFT ===");
        
        // Остановка всех визуальных эффектов
        if (visualEffectsManager != null) {
            visualEffectsManager.stopAllEffects();
            MessageUtil.log("&7✓ Визуальные эффекты остановлены");
        }
        if (soundManager != null) {
            soundManager.stopAllAmbientSounds();
            MessageUtil.log("&7✓ Звуковые эффекты остановлены");
        }
        if (animationManager != null) {
            animationManager.stopAllAnimations();
            MessageUtil.log("&7✓ Анимации остановлены");
        }
        
        // Сохранение всех данных
        saveAllData();
        MessageUtil.log("&7✓ Все данные сохранены");
        
        MessageUtil.log("&c§l✓ UraniumCraft отключен!");
        instance = null;
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);
        getServer().getPluginManager().registerEvents(new RadiationListener(this), this);
        getServer().getPluginManager().registerEvents(new CentrifugeListener(this), this);
        getServer().getPluginManager().registerEvents(new LaboratoryListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleporterListener(this), this);
        getServer().getPluginManager().registerEvents(new AdvancedItemsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new CompactGUIListener(this), this);
    }
    
    private void startPeriodicTasks() {
        // Обновление радиации каждые 5 секунд
        getServer().getScheduler().runTaskTimer(this, () -> {
            radiationManager.updateAllPlayersRadiation();
            
            // Визуальные эффекты радиации
            for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
                int radiation = radiationManager.getPlayerRadiation(player);
                visualEffectsManager.applyRadiationEffects(player, radiation);
                visualEffectsManager.createRadiationAura(player, radiation);
                soundManager.playRadiationSound(player, radiation);
            }
        }, 100L, 100L);
        
        // Работа центрифуг каждые 10 секунд
        getServer().getScheduler().runTaskTimer(this, () -> {
            centrifugeManager.updateAllCentrifuges();
        }, 200L, 200L);
        
        // Процессы лабораторий каждые 30 секунд
        getServer().getScheduler().runTaskTimer(this, () -> {
            laboratoryManager.updateAllLaboratories();
        }, 600L, 600L);
        
        // Автосохранение каждые 5 минут
        getServer().getScheduler().runTaskTimer(this, () -> {
            saveAllData();
        }, 6000L, 6000L);
        
        // Обновление визуальных эффектов структур каждые 20 секунд
        getServer().getScheduler().runTaskTimer(this, () -> {
            updateStructureEffects();
        }, 400L, 400L);
    }
    
    private void updateStructureEffects() {
        // Запуск эффектов для активных структур
        for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
            org.bukkit.Location playerLoc = player.getLocation();
            
            // Проверяем структуры в радиусе 50 блоков
            for (int x = -50; x <= 50; x += 10) {
                for (int z = -50; z <= 50; z += 10) {
                    org.bukkit.Location checkLoc = playerLoc.clone().add(x, 0, z);
                    
                    if (centrifugeManager.isCentrifuge(checkLoc)) {
                        var data = centrifugeManager.getCentrifugeData(checkLoc);
                        if (data != null && data.isRunning()) {
                            visualEffectsManager.createAdvancedCentrifugeEffect(checkLoc);
                        }
                    }
                    
                    if (teleporterManager.isTeleporter(checkLoc)) {
                        visualEffectsManager.startTeleporterEffects(checkLoc);
                    }
                    
                    if (laboratoryManager.isLaboratory(checkLoc)) {
                        visualEffectsManager.startLaboratoryEffects(checkLoc);
                    }
                }
            }
        }
    }
    
    private void saveAllData() {
        if (radiationManager != null) radiationManager.saveAllData();
        if (centrifugeManager != null) centrifugeManager.saveAllData();
        if (laboratoryManager != null) laboratoryManager.saveAllData();
        if (teleporterManager != null) teleporterManager.saveAllData();
        if (achievementManager != null) achievementManager.saveAllData();
        if (playerStatsManager != null) playerStatsManager.saveAllData();
        if (authManager != null) authManager.saveAllData();
    }
    
    public void reload() {
        MessageUtil.log("&e§l=== ПЕРЕЗАГРУЗКА URANIUMCRAFT ===");
        
        configManager.reload();
        radiationManager.reload();
        centrifugeManager.reload();
        laboratoryManager.reload();
        teleporterManager.reload();
        authManager.reload();
        
        // Перезагрузка рецептов
        craftingManager.reloadRecipes();
        
        MessageUtil.log("&a§l✓ Конфигурация плагина перезагружена!");
    }
    
    // Геттеры для всех менеджеров
    public static UraniumCraft getInstance() { return instance; }
    
    // Основные менеджеры
    public RadiationManager getRadiationManager() { return radiationManager; }
    public UraniumItems getUraniumItems() { return uraniumItems; }
    public BalancedCentrifugeManager getCentrifugeManager() { return centrifugeManager; }
    public EnhancedLaboratoryManager getLaboratoryManager() { return laboratoryManager; }
    public TeleporterManager getTeleporterManager() { return teleporterManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public AchievementManager getAchievementManager() { return achievementManager; }
    public PlayerStatsManager getPlayerStatsManager() { return playerStatsManager; }
    public AuthManager getAuthManager() { return authManager; }
    
    // Визуальные менеджеры
    public EnhancedVisualEffectsManager getVisualEffectsManager() { return visualEffectsManager; }
    public HologramManager getHologramManager() { return hologramManager; }
    public SoundManager getSoundManager() { return soundManager; }
    public AnimationManager getAnimationManager() { return animationManager; }
    
    // Новые менеджеры
    public AdvancedCraftingManager getCraftingManager() { return craftingManager; }
    public CompactGUIManager getCompactGUIManager() { return compactGUIManager; }
}
