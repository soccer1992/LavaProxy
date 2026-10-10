package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.HandshakeIntent;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.readers.LoginReader;
import ca.soccer1992.lavaproxy.packets.readers.PreStatusReader;
import ca.soccer1992.lavaproxy.packets.server.HandshakePacket;
import ca.soccer1992.lavaproxy.types.GameProperty;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.InetSocketAddress;
import java.util.UUID;

public class HandshakeHandler extends Handler{
    public boolean handle(Packet p, Connection c){
        if (!(p instanceof HandshakePacket packet)){
            return false;
        }
        if (packet.intent == null){
            return false;
        }
        if (packet.host.contains("\0")){
            if (Main.incomingForwardType.startsWith("bungee")){
                String[] decoded = packet.host.split("\0");
                if (decoded.length >= 3){
                    packet.setHost(decoded[0]);
                    c.addr = new InetSocketAddress(decoded[1], c.addr.getPort());
                    String realUUID = decoded[2];
                    if (decoded[2].length() == 32) realUUID = realUUID.replaceFirst(
                            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                            "$1-$2-$3-$4-$5"
                    );
                    c.plr.setUUID(UUID.fromString(realUUID));
                    if (decoded.length == 4){
                        // properties!
                        try{
                            JsonArray propertiesArray = JsonParser.parseString(decoded[3]).getAsJsonArray();
                            for (JsonElement obj : propertiesArray.asList()){
                                if (obj.isJsonObject()){
                                    JsonObject object = obj.getAsJsonObject();
                                    String name = object.get("name").getAsString();
                                    String value = object.get("value").getAsString();
                                    String sig = object.has("signature") ? object.get("signature").getAsString() : "";
                                    if (name.equals("bungeeguard-token") && Main.incomingForwardType.equals("bungeeguard")){
                                        if (!Main.incomingForwardKey.equals(value)){
                                            c.close();
                                            return true; // INVALID KEY!
                                        }
                                        continue;
                                    }
                                    c.plr.properties.add(new GameProperty(name, value, sig));
                                }
                            }
                        } catch (Exception ignored){
                            // ignore
                        }
                    }

                }
                c.receivedForwarding = true;
            }

        }
        c.connectAddr = new InetSocketAddress(packet.host, packet.port);
        if (packet.proto == null) packet.proto = MinecraftVersions.UNSUPPORTED;
        c.setProtocol(packet.proto);
        switch (packet.intent){
            case HandshakeIntent.STATUS:
                c.setReader(new PreStatusReader());
                c.setHandler(new PreStatusHandler());
                c.conType = ConnectionTypes.PRE_STATUS;
                return true;
            case HandshakeIntent.LOGIN, HandshakeIntent.TRANSFER:
                c.setReader(new LoginReader());
                c.conType = ConnectionTypes.LOGIN;
                c.setHandler(new LoginHandler());
                if (packet.proto == MinecraftVersions.UNSUPPORTED){
                    c.disconnect(ComponentUtils.parser.deserialize(c.fillPlaceholders("error.unsupported","","")), true);
                    return true;
                }
                return true;
        }
        return false;
    }
}
