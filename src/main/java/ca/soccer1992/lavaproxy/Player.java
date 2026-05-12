package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.client.play.SystemChat;
import ca.soccer1992.lavaproxy.packets.server.ClientInfo;
import ca.soccer1992.lavaproxy.types.KnownPack;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.UUID;

public class Player {
    public Connection con;
    public String name = null;
    public UUID uuid = null;
    public String brand = "";
    public ClientInfo infoPacket = null;
    public ArrayList<String> enabled_features = new ArrayList<>();
    public ArrayList<KnownPack> knownPacks;

    public void setKnownPacks(ArrayList<KnownPack> knownPack){
        this.knownPacks = knownPack;

    }

    public void sendMessage(Component msg, boolean isActionBar){
        SystemChat chat = new SystemChat();
        chat.message = msg;
        chat.isActionBar = isActionBar;
        if (con.isBackend) throw new IllegalArgumentException("Cannot sendMessage on a backend connection!");
        if (con.conType != ConnectionTypes.PLAY) throw new IllegalArgumentException("Cannot sendMessage on a non-play connection!");

        con.writePacket(chat);
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
