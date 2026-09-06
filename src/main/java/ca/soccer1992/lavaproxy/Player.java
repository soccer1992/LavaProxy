package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.UnsignedChat;
import ca.soccer1992.lavaproxy.packets.client.play.SystemChat;
import ca.soccer1992.lavaproxy.packets.clientserver.EnterConfiguration;
import ca.soccer1992.lavaproxy.packets.server.ClientInfo;
import ca.soccer1992.lavaproxy.types.KnownPack;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.parser;

public class Player implements CommandSender {
    public Connection con;
    public String name = null;
    public UUID uuid = null;
    public String brand = "";
    public ClientInfo infoPacket = null;
    public ArrayList<String> enabled_features = new ArrayList<>();
    public ArrayList<KnownPack> knownPacks;
    public Map<String, Boolean> permissions = new HashMap<>();

    public void setKnownPacks(ArrayList<KnownPack> knownPack){
        this.knownPacks = knownPack;

    }
    public void transferToServer(String server){
        if (server.equals(con.connectedServer.name)){
            sendMessage(parser.deserialize(con.fillPlaceholders("connect.alreadyConnected", "", brand)), false);
            return;
        }
        if (!Main.servers.containsKey(server.toLowerCase())){
            sendMessage(parser.deserialize(con.fillPlaceholders("connect.notExist", "", brand)), false);
            return;
        }

        con.waitingServer = Main.servers.get(server);
        con.writePacket(new EnterConfiguration());
    }
    public void sendMessage(Component msg, boolean isActionBar){
        Packet p;
        if (con.protocol.isGreaterEquals(MinecraftVersions.MINECRAFT_1_19)) {
            SystemChat chat = new SystemChat();
            chat.message = msg;
            chat.isActionBar = isActionBar;
            p = chat;

        } else {
            UnsignedChat chat = new UnsignedChat();
            chat.sender = UUID.randomUUID();
            chat.type = 1;
            chat.msg = ComponentUtils.json(msg);
            p = chat;
        }

        if (con.isBackend) throw new IllegalArgumentException("Cannot sendMessage on a backend connection!");
        if (con.conType != ConnectionTypes.PLAY) throw new IllegalArgumentException("Cannot sendMessage on a non-play connection!");
        con.writePacket(p);
    }
    public boolean hasPermission(String permission) {
        if (permissions.containsKey(permission)) return permissions.get(permission);
        String[] parts = permission.split("\\.");
        for (int i = parts.length - 1; i > 0; i--) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < i; j++) {
                if (j > 0) sb.append(".");
                sb.append(parts[j]);
            }
            if (permissions.containsKey(sb + ".*")) return permissions.get(sb + ".*");
        }
        return permissions.getOrDefault("*",false);
    }
    public boolean executeCommand(String command){
        if (Main.logCommands) System.out.println(con.fillPlaceholders("log.command", "", brand, "", 0, "", con.connectedServer.name, "/" + command));

        String rootCmd = command.split(" ")[0];
        if (Main.getCommand(rootCmd) == null) {
            return false;
        }
        ParseResults<CommandSender> results = Main.dispatcher.parse(command, this);
        try {
            return Main.dispatcher.execute(results) == 1;
        } catch (CommandSyntaxException e) {
            //sendMessage(Component.text(e.getMessage()), false);
            return false;
        }
    }
    public void setUUID(UUID uuid){
        this.uuid = uuid;

    }
    public void setInfo(ClientInfo info){
        this.infoPacket = info;

    }
    public void setBrand(String brand){
        this.brand = brand;

    }
    public void setName(String name){
        this.name = name;
    }
    public Player(Connection c){
        this.con = c;

    }
    public String toString(){
        if (name != null) {
            return String.format("%s (%s)", name, uuid);
        } else {
            return con.addr.toString(); // yes i know this is probably stupid but idc someone else can fix this
        }
    }
}
