package com.cocobeen.Bukkit;

import com.cocobeen.Bukkit.Commands.AutoRestartCommand;
import com.cocobeen.Bukkit.Listener.PlayerLoginListener;
import com.cocobeen.Bukkit.Listener.ServerLoadListener;
import com.cocobeen.Bukkit.Utils.Base64Encode;
import com.cocobeen.Bukkit.Utils.DataManager;
import com.cocobeen.Bukkit.Utils.DiscordWebhook;
import com.cocobeen.Bukkit.Utils.RestartClient;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AutoRestartPlugin extends JavaPlugin {
    private static AutoRestartPlugin plugin = null;
    private DataManager dataManager = new DataManager(this);
    public static boolean ServerLoginDelay = true;

    @Override
    public void onEnable(){
        plugin = this;

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        dataManager.getDataConfig().options().copyDefaults();
        dataManager.saveDataConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        registerEvents();
        registerCommands();

        initTPSTask();

        initLoginDelayTask();

        initTimingTask();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
        //getServer().getPluginManager().registerEvents(new ServerLoadListener(), this);
    }

    private void registerCommands(){
        getServer().getPluginCommand("autorestart").setExecutor(new AutoRestartCommand());

        getServer().getPluginCommand("autorestart").setTabCompleter(new AutoRestartCommand());
    }

    private void initTPSTask(){
        new BukkitRunnable(){
            @Override
            public void run() {
                double[] tps = getServer().getTPS();
                int tps_one = (int) tps[0];
                int tps_two = (int) tps[1];
                int tps_three = (int) tps[2];

                getLogger().warning("TPS: " + tps_one + " " + tps_two + " " + tps_three);
                int tps_limit = getConfig().getInt("restart-tps-lower-limit");

                if (tps_one < tps_limit && tps_two < tps_limit && tps_three < tps_limit) {
                    String tps_lower_message = getConfig().getString("restart-tps-lower-message");
                    String reason = getConfig().getString("discord-webhook.reason-tps-limit");
                    getServer().broadcast(Component.text(tps_lower_message));

                    SetupAutoRestart(reason);
                }
            }
        }.runTaskTimerAsynchronously(this, 0, 12000);
    }

    private void initLoginDelayTask(){
        int restart_delay = getConfig().getInt("restart-delay");
        new BukkitRunnable(){
            @Override
            public void run() {
                ServerLoginDelay = false;
            }
        }.runTaskLater(this, restart_delay * 20);

        new BukkitRunnable(){
            @Override
            public void run() {
                if (dataManager.getDataConfig().getBoolean("state")){
                    dataManager.getDataConfig().set("state", false);
                    plugin.getDataManager().saveDataConfig();
                    String server_name = getConfig().getString("restart-server");
                    new RestartClient(Base64Encode.EncodeString("restart_done#" + server_name));
                }
            }
        }.runTaskLater(this, (restart_delay + 5) * 20);
    }

    public void SetupAutoRestart(String reason){
        int server_players = getServer().getOnlinePlayers().size();

        if (server_players != 0){
            String wait_server = getConfig().getString("wait-server");

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            String player_names = "";

            for (Player player : getServer().getOnlinePlayers()){
                player_names = player_names + player.getUniqueId() + ",";
                out.writeUTF("Connect");
                out.writeUTF(wait_server);
                player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
            }

            String restart_server = getConfig().getString("restart-server");

            player_names = player_names.substring(0, player_names.length() - 1);

            String client_message = "restart#" + restart_server + "@" + player_names;

            new RestartClient(Base64Encode.EncodeString(client_message));

            int restart_delay = getConfig().getInt("shutdown-delay");

            getDataManager().getDataConfig().set("state", true);
            getDataManager().saveDataConfig();

            ServerLoginDelay = true;

            new BukkitRunnable(){
                @Override
                public void run() {
                    sendDiscordWebhookMessage(reason);
                    getServer().shutdown();
                }
            }.runTaskLaterAsynchronously(this, restart_delay * 20);
            return;
        }
        String server_not_players = getConfig().getString("server-not-players");
        getLogger().warning(server_not_players);
    }

    public void sendDiscordWebhookMessage(String reason){
        String webhook_url = getConfig().getString("discord-webhook.webhook-url");
        String username = getConfig().getString("discord-webhook.username");
        String avatar_url = getConfig().getString("discord-webhook.avatar-url");
        String content = getConfig().getString("discord-webhook.content-message");
        String title = getConfig().getString("discord-webhook.title");
        String description = getConfig().getString("discord-webhook.description");
        String image = getConfig().getString("discord-webhook.image");
        String thumbnail = getConfig().getString("discord-webhook.thumbnail");
        String footer_message = getConfig().getString("discord-webhook.footer-message").replace("{reason}", reason).replace("{time}", getTime());
        String footer_image = getConfig().getString("discord-webhook.footer-image");

        DiscordWebhook webhook = new DiscordWebhook(webhook_url);
        webhook.setUsername(username);
        webhook.setAvatarUrl(avatar_url);
        webhook.setContent(content);
        webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle(title).setDescription(description).setImage(image).setThumbnail(thumbnail).setFooter(footer_message, footer_image).setColor(Color.YELLOW));
        try {
            webhook.execute();
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }

    private String getTime(){
        LocalDateTime DateTime = LocalDateTime.now();
        int year = DateTime.getYear();
        int month = DateTime.getMonthValue();
        int day = DateTime.getDayOfMonth();
        int hour = DateTime.getHour();
        int minute = DateTime.getMinute();
        int second = DateTime.getSecond();
        String time = year + "年 " + month + "/" + day + " " + hour + "點" + minute + "分" + second + "秒";
        return time;
    }

    private void initTimingTask() {
        int delay = 1000 * 60 * 60 * 24;

        int day = getConfig().getInt("restart-time.day");
        int min = getConfig().getInt("restart-time.minute");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, day);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        Date time = calendar.getTime();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String reason = getConfig().getString("discord-webhook.reason-time");
                SetupAutoRestart(reason);
            }
        }, time, delay);
    }

    public static AutoRestartPlugin getPlugin(){
        return plugin;
    }

    public DataManager getDataManager(){
        return dataManager;
    }
}