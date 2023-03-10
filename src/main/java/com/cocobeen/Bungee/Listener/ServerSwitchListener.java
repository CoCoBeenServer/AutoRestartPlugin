package com.cocobeen.Bungee.Listener;

import com.cocobeen.Bungee.AutoRestartPluginBungee;
import com.cocobeen.Bungee.Utils.DataManager;
import com.cocobeen.Bungee.Utils.RestartServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class ServerSwitchListener implements Listener {
    private final AutoRestartPluginBungee plugin = AutoRestartPluginBungee.getPlugin();
    private final DataManager data = plugin.getDataManager();

    @EventHandler
    public void OnServerSwitch(ServerSwitchEvent event){
        ProxiedPlayer player = event.getPlayer();
        String wait_server = data.getConfig().getString("wait-server");

        ServerInfo from = event.getFrom();

        if (from != null && from.getName().equals(wait_server)){
            for (String index : RestartServer.server_index){
                List<ProxiedPlayer> players = RestartServer.restart_map.get(index);
                if (players.contains(player)){
                    String remove_list = data.getConfig().getString("restart-message");
                    plugin.getLogger().warning(remove_list
                            .replace("{player}", player.getName()));
                    players.remove(player);
                }
                RestartServer.restart_map.put(index, players);
            }
        }
    }
}