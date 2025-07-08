package com.uranium.commands;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UraniumAdminCommand implements CommandExecutor {
    
    private final UraniumCraft plugin;
    
    public UraniumAdminCommand(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("uraniumcraft.admin")) {
            MessageUtil.sendMessage(sender, "&cУ вас нет прав для использования этой команды!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                MessageUtil.sendMessage(sender, "&aКонфигурация плагина перезагружена!");
                break;
            case "cleanup":
                if (args.length < 2) {
                    MessageUtil.sendMessage(sender, "&cИспользование: /uraniumadmin cleanup <игрок>");
                    return true;
                }
                cleanupPlayer(sender, args[1]);
                break;
            case "give":
                if (args.length < 3) {
                    MessageUtil.sendMessage(sender, "&cИспользование: /uraniumadmin give <игрок> <предмет>");
                    return true;
                }
                giveItem(sender, args[1], args[2]);
                break;
            case "radiation":
                if (args.length < 3) {
                    MessageUtil.sendMessage(sender, "&cИспользование: /uraniumadmin radiation <игрок> <уровень>");
                    return true;
                }
                setRadiation(sender, args[1], args[2]);
                break;
            default:
                sendHelp(sender);
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        MessageUtil.sendMessage(sender, "&6=== Административные команды ===");
        MessageUtil.sendMessage(sender, "&e/uraniumadmin reload &7- перезагрузить плагин");
        MessageUtil.sendMessage(sender, "&e/uraniumadmin cleanup <игрок> &7- очистить радиацию");
        MessageUtil.sendMessage(sender, "&e/uraniumadmin give <игрок> <предмет> &7- выдать предмет");
        MessageUtil.sendMessage(sender, "&e/uraniumadmin radiation <игрок> <уровень> &7- установить радиацию");
    }
    
    private void cleanupPlayer(CommandSender sender, String playerName) {
        Player target = plugin.getServer().getPlayer(playerName);
        if (target == null) {
            MessageUtil.sendMessage(sender, "&cИгрок не найден!");
            return;
        }
        
        plugin.getRadiationManager().clearPlayerRadiation(target);
        MessageUtil.sendMessage(sender, "&aРадиация игрока " + target.getName() + " очищена!");
        MessageUtil.sendMessage(target, "&aВаша радиация была очищена администратором!");
    }
    
    private void giveItem(CommandSender sender, String playerName, String itemName) {
        Player target = plugin.getServer().getPlayer(playerName);
        if (target == null) {
            MessageUtil.sendMessage(sender, "&cИгрок не найден!");
            return;
        }
        
        org.bukkit.inventory.ItemStack item = getItemByName(itemName);
        if (item == null) {
            MessageUtil.sendMessage(sender, "&cПредмет не найден!");
            return;
        }
        
        target.getInventory().addItem(item);
        MessageUtil.sendMessage(sender, "&aПредмет выдан игроку " + target.getName() + "!");
        MessageUtil.sendMessage(target, "&aВы получили предмет от администратора!");
    }
    
    private void setRadiation(CommandSender sender, String playerName, String radiationStr) {
        Player target = plugin.getServer().getPlayer(playerName);
        if (target == null) {
            MessageUtil.sendMessage(sender, "&cИгрок не найден!");
            return;
        }
        
        try {
            int radiation = Integer.parseInt(radiationStr);
            plugin.getRadiationManager().setPlayerRadiation(target, radiation);
            MessageUtil.sendMessage(sender, "&aРадиация игрока " + target.getName() + " установлена на " + radiation + " рад!");
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(sender, "&cНеверный формат числа!");
        }
    }
    
    private org.bukkit.inventory.ItemStack getItemByName(String name) {
        return switch (name.toLowerCase()) {
            case "uranium_dust" -> plugin.getUraniumItems().getUraniumDust();
            case "uranium_ingot" -> plugin.getUraniumItems().getUraniumIngot();
            case "uranium_block" -> plugin.getUraniumItems().getUraniumBlock();
            case "chem_helmet" -> plugin.getUraniumItems().getChemProtectionHelmet();
            case "chem_chestplate" -> plugin.getUraniumItems().getChemProtectionChestplate();
            case "power_helmet" -> plugin.getUraniumItems().getPowerArmorHelmet();
            case "power_chestplate" -> plugin.getUraniumItems().getPowerArmorChestplate();
            case "railgun" -> plugin.getUraniumItems().getRailgun();
            case "uranium_tablet" -> plugin.getUraniumItems().getUraniumTablet();
            case "geiger_counter" -> plugin.getUraniumItems().getGeigerCounter();
            case "centrifuge_core" -> plugin.getUraniumItems().getCentrifugeCore();
            case "laboratory_terminal" -> plugin.getUraniumItems().getLaboratoryTerminal();
            case "teleport_core" -> plugin.getUraniumItems().getTeleportCore();
            default -> null;
        };
    }
}
