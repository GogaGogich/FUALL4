package com.uranium.commands;

import com.uranium.UraniumCraft;
import com.uranium.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuthCommand implements CommandExecutor {
    
    private final UraniumCraft plugin;
    
    public AuthCommand(UraniumCraft plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "&cТолько для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            MessageUtil.sendMessage(player, "&6=== АВТОРИЗАЦИЯ URANIUMCRAFT ===");
            MessageUtil.sendMessage(player, "&eИспользование: &f/uraniumauth <код>");
            MessageUtil.sendMessage(player, "&7Обратитесь к администратору за кодом доступа");
            MessageUtil.sendMessage(player, "&8Без авторизации взаимодействие с плагином невозможно");
            return true;
        }
        
        String code = String.join(" ", args);
        plugin.getAuthManager().startAuth(player, code);
        
        return true;
    }
}
