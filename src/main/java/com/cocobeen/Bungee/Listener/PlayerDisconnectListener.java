package com.cocobeen.Bungee.Listener;

import com.cocobeen.Bungee.Utils.RestartServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void OnPlayerDisconnect(PlayerDisconnectEvent event){
        ProxiedPlayer player = event.getPlayer();
        for (String index : RestartServer.server_index){
            List<ProxiedPlayer> players = RestartServer.restart_map.get(index);
            if (players.contains(player)){
                players.remove(player);
            }
            RestartServer.restart_map.put(index, players);
        }
    }
}