package com.cocobeen.Bungee.Utils;

import com.cocobeen.Bungee.AutoRestartPluginBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class DataManager {
    private AutoRestartPluginBungee plugin;

    public DataManager(AutoRestartPluginBungee plugin){
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public Configuration getConfig(){
        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "bungeeconfig.yml"));
            return configuration;
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
        return null;
    }

    public void saveDefaultConfig(){
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), "bungeeconfig.yml");

        if (!file.exists()) {

            try (InputStream in = plugin.getResourceAsStream("bungeeconfig.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveConfig(){
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), new File(plugin.getDataFolder(), "bungeeconfig.yml"));
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }
}