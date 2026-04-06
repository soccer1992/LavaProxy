package ca.soccer1992.lavaproxy;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {
    public static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static final AttributeKey<Connection> READER =
            AttributeKey.valueOf("connection");
    public static final AttributeKey<Connection> BACKEND =
            AttributeKey.valueOf("backend");
    public static final String[] trys = new String[]{"lobby"};
    public static final HashMap<String, String> translations = new HashMap<>();

    public static final HashMap<String, ArrayList<Object>> servers = new HashMap<>();
    public static boolean logErrors = true;
    public static boolean logPings = true;
    public static int CON_AMOUNT = 0;

    public static void main(String[] args) throws Exception {
        servers.put("lobby", new ArrayList<>(List.of("127.0.0.1",25565)));
        translations.put("backend.player.disconnect","<red>You have been disconnected from {serverName}: {message}</red>");
        translations.put("log.ping","{ip} has pinged");
        translations.put("backend.transfer","{player} is getting transfered to: {host}:{port}");

        translations.put("log.connect","{player} ({ipHost}) has started login.");
        translations.put("log.disconnect","{player} has disconnected for: {message}");
        translations.put("log.brand","{player} brand: {brand}");

        translations.put("backend.disconnect","{player} has disconnected from {serverName}: {message}");

        //System.out.println(root.value.values());
        //root = new CompoundTag("root");
        //root.put(new StringTag("name","hello"));
        //System.out.println(root.);
        //NBTWriter.write(root, new FileOutputStream("world.dat"), false, true); // true = gzip

        new NettyServer(25577).start();

    }
}