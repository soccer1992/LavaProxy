package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.UnsignedChat;
import ca.soccer1992.lavaproxy.packets.client.play.SystemChat;
import ca.soccer1992.lavaproxy.packets.server.ClientInfo;
import ca.soccer1992.lavaproxy.types.KnownPack;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.UUID;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.parser;

public class Player {
    public Connection con;
    public String name = null;
    public UUID uuid = null;
    public String brand = "";
    public ClientInfo infoPacket = null;
    public ArrayList<String> enabled_features = new ArrayList<>();
    public ArrayList<KnownPack> knownPacks;
    public ArrayList<String> permissions = new ArrayList<>();

    public void setKnownPacks(ArrayList<KnownPack> knownPack){
        this.knownPacks = knownPack;

    }
    public void transferToServer(String server){
        if (server.equals(con.connectedServer)){
            sendMessage(parser.deserialize(con.fillPlaceholders("connect.alreadyConnected", "", brand)), false);
            return;
        }
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
        if (permissions.contains(permission)) return true;
        if (permissions.contains("*")) return true;

        String[] parts = permission.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) sb.append(".");
            sb.append(parts[i]);
            if (permissions.contains(sb + ".*")) return true;
        }
        return false;
    }
    public boolean executeCommand(String command){
        String rootCmd = command.split(" ")[0];
        if (getCommand(rootCmd) == null) {
            return false;
        }
        ParseResults<Player> results = Main.dispatcher.parse(command, this);
        try {
            return Main.dispatcher.execute(results) == 1;
        } catch (CommandSyntaxException e) {
            sendMessage(Component.text(e.getMessage()), false);
            return true;
        }
    }
    public CommandNode getCommand(String cmd){
        return Main.dispatcher.getRoot().getChild(cmd);
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
