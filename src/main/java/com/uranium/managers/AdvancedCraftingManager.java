package com.uranium.managers;

import com.uranium.UraniumCraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class AdvancedCraftingManager {
    
    private final UraniumCraft plugin;
    private final Map<String, Recipe> customRecipes = new HashMap<>();
    
    public AdvancedCraftingManager(UraniumCraft plugin) {
        this.plugin = plugin;
        registerAllRecipes();
    }
    
    private void registerAllRecipes() {
        // Базовые материалы (очень сложные)
        registerUraniumIngotRecipe();
        registerUraniumBlockRecipe();
        registerUraniumCapsuleRecipe();
        
        // Квантовый процессор (самый сложный крафт)
        registerQuantumProcessorRecipe();
        
        // Строительные блоки (требуют квантовый процессор + много ресурсов)
        registerCentrifugeCoreRecipe();
        registerTeleportCoreRecipe();
        registerLaboratoryTerminalRecipe();
        
        // Инструменты (сложные рецепты)
        registerGeigerCounterRecipe();
        registerUraniumTabletRecipe();
        
        // Базовая химзащита (доступна без исследований, но дорогая)
        registerBasicChemProtectionRecipes();
        
        // Продвинутые компоненты
        registerAdvancedComponentsRecipes();
    }
    
    // === БАЗОВЫЕ МАТЕРИАЛЫ ===
    
    private void registerUraniumIngotRecipe() {
        ItemStack result = plugin.getUraniumItems().getUraniumIngot();
        result.setAmount(1);
        
        NamespacedKey key = new NamespacedKey(plugin, "uranium_ingot");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "DDD",
            "DGD", 
            "DDD"
        );
        recipe.setIngredient('D', plugin.getUraniumItems().getUraniumDust().getType());
        recipe.setIngredient('G', Material.GOLD_INGOT); // Требует золото для стабилизации
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("uranium_ingot", recipe);
    }
    
    private void registerUraniumBlockRecipe() {
        ItemStack result = plugin.getUraniumItems().getUraniumBlock();
        result.setAmount(1);
        
        NamespacedKey key = new NamespacedKey(plugin, "uranium_block");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "ILI",
            "LIL",
            "ILI"
        );
        recipe.setIngredient('I', plugin.getUraniumItems().getUraniumIngot().getType());
        recipe.setIngredient('L', Material.IRON_BLOCK); // Железная защита (вместо свинца)
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("uranium_block", recipe);
    }
    
    private void registerUraniumCapsuleRecipe() {
        ItemStack result = plugin.getUraniumItems().getUraniumCapsule();
        result.setAmount(1);
        
        NamespacedKey key = new NamespacedKey(plugin, "uranium_capsule");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "LIL",
            "IGI",
            "LIL"
        );
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('L', Material.IRON_BLOCK); // Железо вместо свинца
        recipe.setIngredient('G', Material.GLASS);
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("uranium_capsule", recipe);
    }
    
    // === КВАНТОВЫЙ ПРОЦЕССОР (САМЫЙ СЛОЖНЫЙ) ===
    
    private void registerQuantumProcessorRecipe() {
        ItemStack result = plugin.getUraniumItems().getQuantumProcessor();
        result.setAmount(1);
        
        NamespacedKey key = new NamespacedKey(plugin, "quantum_processor");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "DED",
            "ERE",
            "DED"
        );
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('E', Material.EMERALD);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("quantum_processor", recipe);
    }
    
    // === СТРОИТЕЛЬНЫЕ БЛОКИ ===
    
    private void registerCentrifugeCoreRecipe() {
        ItemStack result = plugin.getUraniumItems().getCentrifugeCore();
        result.setAmount(1); // Теперь только 1 штука
        
        NamespacedKey key = new NamespacedKey(plugin, "centrifuge_core");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "IRI",
            "RQR",
            "IRI"
        );
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        recipe.setIngredient('Q', plugin.getUraniumItems().getQuantumProcessor().getType());
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("centrifuge_core", recipe);
    }
    
    private void registerTeleportCoreRecipe() {
        ItemStack result = plugin.getUraniumItems().getTeleportCore();
        result.setAmount(1); // Теперь только 1 штука
        
        NamespacedKey key = new NamespacedKey(plugin, "teleport_core");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "EPE",
            "PQP",
            "EPE"
        );
        recipe.setIngredient('E', Material.ENDER_PEARL);
        recipe.setIngredient('P', Material.END_PORTAL_FRAME);
        recipe.setIngredient('Q', plugin.getUraniumItems().getQuantumProcessor().getType());
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("teleport_core", recipe);
    }
    
    private void registerLaboratoryTerminalRecipe() {
        ItemStack result = plugin.getUraniumItems().getLaboratoryTerminal();
        result.setAmount(1);
        
        NamespacedKey key = new NamespacedKey(plugin, "laboratory_terminal");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "DQD",
            "QEQ",
            "DQD"
        );
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('Q', plugin.getUraniumItems().getQuantumProcessor().getType());
        recipe.setIngredient('E', Material.ENCHANTING_TABLE);
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("laboratory_terminal", recipe);
    }
    
    // === ИНСТРУМЕНТЫ ===
    
    private void registerGeigerCounterRecipe() {
        ItemStack result = plugin.getUraniumItems().getGeigerCounter();
        result.setAmount(1);
        
        NamespacedKey key = new NamespacedKey(plugin, "geiger_counter");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "IRI",
            "RCR",
            "IGI"
        );
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('C', Material.COMPASS);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("geiger_counter", recipe);
    }
    
    private void registerUraniumTabletRecipe() {
        ItemStack result = plugin.getUraniumItems().getUraniumTablet();
        result.setAmount(1);
        
        NamespacedKey key = new NamespacedKey(plugin, "uranium_tablet");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        
        recipe.shape(
            "GQG",
            "QCQ",
            "GRG"
        );
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('Q', plugin.getUraniumItems().getQuantumProcessor().getType());
        recipe.setIngredient('C', Material.CLOCK);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        
        Bukkit.addRecipe(recipe);
        customRecipes.put("uranium_tablet", recipe);
    }
    
    // === БАЗОВАЯ ХИМЗАЩИТА ===
    
    private void registerBasicChemProtectionRecipes() {
        // Шлем химзащиты
        ItemStack helmet = plugin.getUraniumItems().getChemProtectionHelmet();
        NamespacedKey helmetKey = new NamespacedKey(plugin, "chem_helmet");
        ShapedRecipe helmetRecipe = new ShapedRecipe(helmetKey, helmet);
        helmetRecipe.shape(
            "LIL",
            "L L",
            "   "
        );
        helmetRecipe.setIngredient('L', Material.LEATHER);
        helmetRecipe.setIngredient('I', Material.IRON_INGOT);
        Bukkit.addRecipe(helmetRecipe);
        customRecipes.put("chem_helmet", helmetRecipe);
        
        // Костюм химзащиты
        ItemStack chestplate = plugin.getUraniumItems().getChemProtectionChestplate();
        NamespacedKey chestKey = new NamespacedKey(plugin, "chem_chestplate");
        ShapedRecipe chestRecipe = new ShapedRecipe(chestKey, chestplate);
        chestRecipe.shape(
            "L L",
            "LIL",
            "LLL"
        );
        chestRecipe.setIngredient('L', Material.LEATHER);
        chestRecipe.setIngredient('I', Material.IRON_BLOCK);
        Bukkit.addRecipe(chestRecipe);
        customRecipes.put("chem_chestplate", chestRecipe);
        
        // Штаны химзащиты
        ItemStack leggings = plugin.getUraniumItems().getChemProtectionLeggings();
        NamespacedKey legsKey = new NamespacedKey(plugin, "chem_leggings");
        ShapedRecipe legsRecipe = new ShapedRecipe(legsKey, leggings);
        legsRecipe.shape(
            "LIL",
            "L L",
            "L L"
        );
        legsRecipe.setIngredient('L', Material.LEATHER);
        legsRecipe.setIngredient('I', Material.IRON_INGOT);
        Bukkit.addRecipe(legsRecipe);
        customRecipes.put("chem_leggings", legsRecipe);
        
        // Ботинки химзащиты
        ItemStack boots = plugin.getUraniumItems().getChemProtectionBoots();
        NamespacedKey bootsKey = new NamespacedKey(plugin, "chem_boots");
        ShapedRecipe bootsRecipe = new ShapedRecipe(bootsKey, boots);
        bootsRecipe.shape(
            "   ",
            "L L",
            "I I"
        );
        bootsRecipe.setIngredient('L', Material.LEATHER);
        bootsRecipe.setIngredient('I', Material.IRON_INGOT);
        Bukkit.addRecipe(bootsRecipe);
        customRecipes.put("chem_boots", bootsRecipe);
    }
    
    // === ПРОДВИНУТЫЕ КОМПОНЕНТЫ ===
    
    private void registerAdvancedComponentsRecipes() {
        // Энергетический кристалл
        ItemStack energyCrystal = new ItemStack(Material.NETHER_STAR);
        NamespacedKey crystalKey = new NamespacedKey(plugin, "energy_crystal");
        ShapedRecipe crystalRecipe = new ShapedRecipe(crystalKey, energyCrystal);
        crystalRecipe.shape(
            "DED",
            "EQE",
            "DED"
        );
        crystalRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        crystalRecipe.setIngredient('E', Material.EMERALD_BLOCK);
        crystalRecipe.setIngredient('Q', plugin.getUraniumItems().getQuantumProcessor().getType());
        Bukkit.addRecipe(crystalRecipe);
        customRecipes.put("energy_crystal", crystalRecipe);
        
        // Стабилизатор материи
        ItemStack stabilizer = new ItemStack(Material.BEACON);
        NamespacedKey stabKey = new NamespacedKey(plugin, "matter_stabilizer");
        ShapedRecipe stabRecipe = new ShapedRecipe(stabKey, stabilizer);
        stabRecipe.shape(
            "GQG",
            "QNQ",
            "GQG"
        );
        stabRecipe.setIngredient('G', Material.GOLD_BLOCK);
        stabRecipe.setIngredient('Q', plugin.getUraniumItems().getQuantumProcessor().getType());
        stabRecipe.setIngredient('N', Material.NETHER_STAR);
        Bukkit.addRecipe(stabRecipe);
        customRecipes.put("matter_stabilizer", stabRecipe);
    }
    
    public Map<String, Recipe> getCustomRecipes() {
        return customRecipes;
    }
    
    public void reloadRecipes() {
        // Удаляем старые рецепты
        for (Recipe recipe : customRecipes.values()) {
            try {
                Bukkit.removeRecipe(recipe.getResult().getType().getKey());
            } catch (Exception e) {
                // Игнорируем ошибки удаления
            }
        }
        customRecipes.clear();
        
        // Регистрируем заново
        registerAllRecipes();
    }
}
