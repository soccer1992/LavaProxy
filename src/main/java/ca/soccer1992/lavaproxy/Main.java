package ca.soccer1992.lavaproxy;
import ca.soccer1992.lavaproxy.commands.ServerCommand;
import ca.soccer1992.lavaproxy.types.ServerDefinition;
import com.moandjiezana.toml.Toml;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.AttributeKey;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Main {
    public static final AttributeKey<Connection> READER =
            AttributeKey.valueOf("connection");
    public static final AttributeKey<Connection> BACKEND =
            AttributeKey.valueOf("backend");
    public static String[] trys = null;
    public static final HashMap<String, String> translations = new HashMap<>();
    public static CommandDispatcher<Player> dispatcher = new CommandDispatcher<>();
    public static final EventLoopGroup nettyGroup = new NioEventLoopGroup();
    public static final EventLoopGroup bossGroup = new NioEventLoopGroup(4);
    public static String motd;

    public static final HashMap<String, ServerDefinition> servers = new HashMap<>();
    public static boolean logErrors;
    public static boolean logPings;
    public static boolean logCommands;

    public static int CON_AMOUNT = 0;
    public static final Map<UUID, Player> players = new ConcurrentHashMap<>();
    public static String forwardType = "none";
    public static String forwardKey = "";

    public static void main(String[] args) throws Exception {
        translations.put("backend.player.disconnect","<red>You have been disconnected from {serverName}: {message}</red>");
        translations.put("log.ping","{ip} has pinged");
        translations.put("log.command","{player} has ran command: {command}");

        translations.put("backend.transfer","{player} is getting transfered to: {host}:{port}");
        translations.put("log.connect","{player} ({ipHost}) has started login.");
        translations.put("log.connected","{player} has connected to {serverName}.");
        translations.put("error.unsupported","<red>Your protocol is too old/new for LavaProxy.</red>");
        translations.put("log.disconnect","{player} has disconnected for: {message}");
        translations.put("log.brand","{player} brand: {brand}");
        translations.put("backend.disconnect","{player} has disconnected from {serverName}: {message}");
        translations.put("backend.brand","{backendBrand} [LavaProxy]");
        translations.put("connect.alreadyConnected","<red>You are already connected to this server</red>");
        translations.put("connect.notExist","<red>This server does not exist</red>");
        translations.put("command.server.hover_msg","Connect to {serverName}");
        translations.put("command.server.default_msg","<yellow>You are currently connected to {serverName}</yellow>");
        translations.put("command.server.too_many","<red>There are too many servers to list.</red>");

        File config = new File("config.toml");

        if (!config.exists()) {
            try (InputStream in = Main.class.getResourceAsStream("/config.toml")) {
                if (in == null) {
                    throw new IOException("Missing config.toml resource");
                }

                Files.copy(in, config.toPath());
            }
        }

        Toml toml = new Toml().read(config);
        Toml logging = toml.getTable("logging");
        Toml serverSettings = toml.getTable("server-settings");
        Toml settings = toml.getTable("settings");
        motd = settings.getString("motd");
        logCommands = logging.getBoolean("commands");

        logErrors = logging.getBoolean("errors");
        logPings = logging.getBoolean("pings");
        trys = toml.getList("tries").toArray(new String[0]);
        int hostPort = settings.getLong("port",25577L).intValue();
        forwardType = serverSettings.getString("forward-mode", "").toLowerCase();
        forwardKey = serverSettings.getString("forward-key", "");

        switch (forwardType){
            case "none":
            case "bungeecord":
                // ignore
                break;
            case "bungeeguard":
                if (forwardKey.isEmpty()){
                    System.out.println("Forward key is not defined in key-requiring mode! Defaulting to none.");
                } else {
                    break;
                }
            default:
                forwardType = "none";
                break;
        }
        Map<String, Object> servs = toml.getTable("servers").toMap();
        for (String i : servs.keySet()){
            if (servers.containsKey(i.toLowerCase())){
                System.out.printf("Error loading server %s: Duplicate server name", i);
                continue;
            }
            String ip = toml.getTable("servers").getString(i);
            if (ip.split(":").length > 2){
                System.out.printf("Error loading server %s: Invalid IP%n", i);
                continue;
            }
            String[] ipport = ip.split(":");
            String port = ipport.length == 2 ? ipport[1] : "25565";
            boolean isPort = port.matches("\\d+") && Integer.parseInt(port) <= 65535;
            if (!isPort){
                System.out.printf("Error loading server %s: Invalid Port%n", i);
                continue;
            }
            ServerDefinition definition =  new ServerDefinition(i.toLowerCase(), ipport[0],Integer.parseInt(port));
            definition.forwardType = forwardType;
            definition.forwardKey = forwardKey;

            servers.put(i.toLowerCase(), definition);
        }
        ArrayList<String> newTrys = new ArrayList<>();
        for (String i : trys){
            if (servers.containsKey(i.toLowerCase())) {
                newTrys.add(i.toLowerCase());
            } else {
                System.out.printf("[WARN] Server %s does not exist, try will not be used.%n",i);
            }

        }
        trys = newTrys.toArray(String[]::new);
        if (trys.length == 0) System.out.println("[WARN] No tries loaded, connections will fail!");
        if (servers.isEmpty()) System.out.println("[WARN] No servers loaded, connections will fail!");
        System.out.println("Loaded " + servers.size() + " server(s).");
        dispatcher.register(
                ServerCommand.create()
        );

        //System.out.println(root.value.values());
        //root = new CompoundTag("root");
        //root.put(new StringTag("name","hello"));
        //System.out.println(root.);
        //NBTWriter.write(root, new FileOutputStream("world.dat"), false, true); // true = gzip

        new NettyServer(hostPort).start();

    }
}