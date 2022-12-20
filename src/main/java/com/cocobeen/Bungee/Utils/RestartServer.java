package com.cocobeen.Bungee.Utils;

import com.cocobeen.Bungee.AutoRestartPluginBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RestartServer extends Thread{
    private final AutoRestartPluginBungee plugin = AutoRestartPluginBungee.getPlugin();
    private final DataManager dataManager = plugin.getDataManager();
    private boolean OutServer = false;
    private ServerSocket server = null;
    public static List<String> server_index = new ArrayList<String>();
    public static HashMap<String, List<ProxiedPlayer>> restart_map = new HashMap<>();

    public RestartServer() {
        int port = dataManager.getConfig().getInt("socket.port");
        try {
            server = new ServerSocket(port);
        } catch (IOException exception) {
            plugin.getLogger().warning("Socket error");
            exception.printStackTrace();
        }
    }

    public void run(){
        Socket socket = null;
        BufferedInputStream in = null;

        while (!OutServer) {
            try {
                synchronized (server){
                    socket = server.accept();
                }

                socket.setSoTimeout(15000);
                in = new BufferedInputStream(socket.getInputStream());
                byte[] b = new byte[1024];
                String data = "";
                int length;
                while ((length = in.read(b)) > 0){
                    data += new String(b, 0, length);
                }

                String decode_data = Base64Decode.DecodeString(data);

                plugin.getLogger().warning("---------- AutoRestartPluginBungee DEBUG ----------");
                plugin.getLogger().warning(decode_data);
                plugin.getLogger().warning("---------- AutoRestartPluginBungee DEBUG ----------");

                String[] socket_data = decode_data.split("#");
                String[] server_name = socket_data[1].split("@");

                switch (socket_data[0]){
                    case "restart":{
                        if (!server_index.contains(socket_data[0]) && !restart_map.containsKey(socket_data[0])){
                            server_index.add(server_name[0]);
                            List<ProxiedPlayer> ProxiedPlayers = new ArrayList<ProxiedPlayer>();

                            String[] restart_players = server_name[1].split(",");
                            for (String id : restart_players){
                                UUID uuid = UUID.fromString(id);
                                ProxiedPlayers.add(plugin.getProxy().getPlayer(uuid));
                            }
                            restart_map.put(server_name[0], ProxiedPlayers);
                        }
                        break;
                    }
                    case "restart_done":{
                        if (restart_map.containsKey(server_name[0]) && server_index.contains(server_name[0])){
                            List<ProxiedPlayer> ProxiedPlayers = restart_map.get(server_name[0]);
                            ServerInfo servers = plugin.getProxy().getServerInfo(server_name[0]);

                            for (ProxiedPlayer player : ProxiedPlayers){
                                player.connect(servers);
                            }

                            restart_map.remove(server_name[0]);
                            server_index.remove(server_name[0]);
                        }
                        break;
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}