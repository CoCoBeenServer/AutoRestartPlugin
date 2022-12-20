package com.cocobeen.Bukkit.Utils;

import com.cocobeen.Bukkit.AutoRestartPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {
    private AutoRestartPlugin plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public DataManager(AutoRestartPlugin plugin){
        this.plugin = plugin;
        saveDefualtConfig();
    }

    public void reloadConfig() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), "tmp.yml");

        dataConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource("tmp.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getDataConfig() {
        if (dataConfig == null)
            reloadConfig();

        return dataConfig;
    }

    public void saveDataConfig() {
        if (dataConfig == null || configFile == null)
            return;

        try {
            getDataConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }

    public void saveDefualtConfig() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), "tmp.yml");

        if (!configFile.exists())
            plugin.saveResource("tmp.yml", false);
    }
}