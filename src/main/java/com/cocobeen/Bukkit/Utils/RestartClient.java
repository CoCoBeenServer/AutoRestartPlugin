package com.cocobeen.Bukkit.Utils;

import com.cocobeen.Bukkit.AutoRestartPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RestartClient extends Thread {
    private final AutoRestartPlugin plugin = AutoRestartPlugin.getPlugin();
    private final FileConfiguration config = plugin.getConfig();

    public RestartClient(String sender){
        String address = config.getString("socket.host");
        int port = config.getInt("socket.port");
        int timeout = config.getInt("socket.timeout");

        InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);

        Socket client = new Socket();

        try {
            client.connect(inetSocketAddress, timeout);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            out.write(sender.getBytes());
            out.flush();
            out.close();
            client.close();
        } catch (IOException exception) {
            plugin.getLogger().warning("Socket error");
            exception.printStackTrace();
        }
    }
}