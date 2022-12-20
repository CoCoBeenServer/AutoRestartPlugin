package com.cocobeen.Bukkit.Listener;

import com.cocobeen.Bukkit.AutoRestartPlugin;
import com.cocobeen.Bukkit.Utils.Base64Encode;
import com.cocobeen.Bukkit.Utils.RestartClient;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerLoadListener implements Listener {
    private final AutoRestartPlugin plugin = AutoRestartPlugin.getPlugin();
    private final FileConfiguration config = plugin.getConfig();
    private final FileConfiguration dataConfig = plugin.getDataManager().getDataConfig();

    @EventHandler
    public void OnServerLoad(ServerLoadEvent event){
        int restart_delay = config.getInt("restart-delay");

        if (event.getType().equals(ServerLoadEvent.LoadType.STARTUP)){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (dataConfig.getBoolean("state")){
                        dataConfig.set("state", false);
                        plugin.getDataManager().saveDataConfig();
                        String server_name = config.getString("restart-server");
                        new RestartClient(Base64Encode.EncodeString("restart_done#" + server_name));
                    }
                }
            }.runTaskLater(plugin, (restart_delay + 10) * 20);
        }
    }
}