package com.cocobeen.Bukkit.Listener;

import com.cocobeen.Bukkit.AutoRestartPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {
    private final AutoRestartPlugin plugin = AutoRestartPlugin.getPlugin();
    private final FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void OnPlayerLogin(PlayerLoginEvent event){
        if (AutoRestartPlugin.ServerLoginDelay){
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            String kick_message = config.getString("kick-player-message");
            event.kickMessage(Component.text(kick_message));
        }
    }
}