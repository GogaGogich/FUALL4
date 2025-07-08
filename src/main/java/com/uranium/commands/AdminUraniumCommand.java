package com.uranium.commands;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminUraniumCommand implements CommandExecutor {
    
    private final UraniumCraft plugin;
    
    public AdminUraniumCommand(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Только для админов
        if (!sender.hasPermission("uraniumcraft.admin")) {
            MessageUtil.sendMessage(sender, "&cУ вас нет прав для использования этой команды!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "&cТолько для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getCompactGUIManager().openMainMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "guide":
            case "руководство":
                plugin.getCompactGUIManager().openGuide(player);
                break;
            case "items":
            case "предметы":
                plugin.getCompactGUIManager().openItemCatalog(player);
                break;
            case "achievements":
            case "достижения":
                plugin.getCompactGUIManager().openAchievements(player);
                break;
            case "radiation":
            case "радиация":
                showRadiation(player);
                break;
            case "auth":
            case "авторизация":
                handleAuthCommand(player, args);
                break;
            case "help":
            case "помощь":
                showHelp(player);
                break;
            default:
                MessageUtil.sendMessage(player, "&cНеизвестная команда! Используйте /uranium help");
        }
        
        return true;
    }
    
    private void handleAuthCommand(Player player, String[] args) {
        if (args.length < 3) {
            MessageUtil.sendMessage(player, "&cИспользование: /uranium auth <grant/revoke> <игрок>");
            return;
        }
        
        String action = args[1].toLowerCase();
        String targetName = args[2];
        Player target = plugin.getServer().getPlayer(targetName);
        
        if (target == null) {
            MessageUtil.sendMessage(player, "&cИгрок не найден!");
            return;
        }
        
        switch (action) {
            case "grant":
                plugin.getAuthManager().grantAuth(target);
                MessageUtil.sendMessage(player, "&aДоступ предоставлен игроку " + target.getName());
                break;
            case "revoke":
                plugin.getAuthManager().revokeAuth(target);
                MessageUtil.sendMessage(player, "&cДоступ отозван у игрока " + target.getName());
                break;
            default:
                MessageUtil.sendMessage(player, "&cНеизвестное действие! Используйте grant или revoke");
        }
    }
    
    private void showRadiation(Player player) {
        int radiation = plugin.getRadiationManager().getPlayerRadiation(player);
        var level = plugin.getRadiationManager().getRadiationLevel(radiation);
        
        MessageUtil.sendMessage(player, "&6=== ☢ РАДИАЦИЯ ☢ ===");
        MessageUtil.sendMessage(player, "&7Уровень: &f" + radiation + " рад");
        MessageUtil.sendMessage(player, "&7Опасность: &f" + level.name());
        
        if (radiation > 0) {
            MessageUtil.sendMessage(player, "&c⚠ Рекомендуется защита!");
        } else {
            MessageUtil.sendMessage(player, "&a✓ Безопасный уровень");
        }
    }
    
    private void showHelp(Player player) {
        MessageUtil.sendMessage(player, "&6=== ⚛ АДМИН ПОМОЩЬ ⚛ ===");
        MessageUtil.sendMessage(player, "&e/uranium &8- главное меню (только админ)");
        MessageUtil.sendMessage(player, "&e/uranium guide &8- руководство");
        MessageUtil.sendMessage(player, "&e/uranium items &8- каталог предметов");
        MessageUtil.sendMessage(player, "&e/uranium achievements &8- достижения");
        MessageUtil.sendMessage(player, "&e/uranium radiation &8- статус радиации");
        MessageUtil.sendMessage(player, "&e/uranium auth grant <игрок> &8- дать доступ");
        MessageUtil.sendMessage(player, "&e/uranium auth revoke <игрок> &8- отозвать доступ");
        MessageUtil.sendMessage(player, "&e/uranium help &8- эта справка");
    }
}
