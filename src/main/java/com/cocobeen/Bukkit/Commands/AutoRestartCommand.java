package com.cocobeen.Bukkit.Commands;

import com.cocobeen.Bukkit.AutoRestartPlugin;
import com.cocobeen.Bukkit.Utils.RestartClient;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoRestartCommand implements CommandExecutor, TabCompleter {
    private final AutoRestartPlugin plugin = AutoRestartPlugin.getPlugin();
    private final FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equals("restart") && sender.hasPermission("autorestart.admin")){
            int server_players = plugin.getServer().getOnlinePlayers().size();

            if (server_players == 0){
                String message = config.getString("server-not-players");
                sender.sendMessage(Component.text(message));
                return true;
            }

            String message = config.getString("restart");
            sender.sendMessage(Component.text(message));

            String reason = config.getString("discord-webhook.reason-use-command");

            plugin.SetupAutoRestart(reason);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("autorestart.admin")){
            List<String> tab = new ArrayList<String>();
            tab.add("restart");
            return tab;
        }
        return null;
    }
}