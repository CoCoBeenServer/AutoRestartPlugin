package com.cocobeen.Bungee;

import com.cocobeen.Bungee.Listener.PlayerDisconnectListener;
import com.cocobeen.Bungee.Listener.ServerSwitchListener;
import com.cocobeen.Bungee.Utils.DataManager;
import com.cocobeen.Bungee.Utils.RestartServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class AutoRestartPluginBungee extends Plugin {
    private static AutoRestartPluginBungee plugin = null;
    private DataManager dataManager = new DataManager(this);
    private RestartServer restartServer = null;

    @Override
    public void onEnable(){
        plugin = this;

        dataManager.saveDefaultConfig();
        dataManager.getConfig();

        registerEvents();

        restartServer = new RestartServer();
        restartServer.start();

        initRestartIsAliveThreadTask();
    }

    @Override
    public void onDisable(){
        // Plugin shutdown logic
    }

    private void registerEvents(){
        getProxy().getPluginManager().registerListener(this, new ServerSwitchListener());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener());
    }

    private void initRestartIsAliveThreadTask(){
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                if (!restartServer.isAlive()){
                    restartServer.start();
                }
            }
        }, 0, 3, TimeUnit.HOURS);
    }

    public static AutoRestartPluginBungee getPlugin(){
        return plugin;
    }

    public DataManager getDataManager(){
        return dataManager;
    }
}