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
import ca.soccer1992.lavaproxy.utils.ComponentUtils;

import java.net.InetSocketAddress;

public class HandshakeHandler extends Handler{
    public boolean handle(Packet p, Connection c){
        if (!(p instanceof HandshakePacket packet)){
            return false;
        }
        if (packet.intent == null){
            return false;
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
                    c.disconnect(ComponentUtils.parser.deserialize(c.fillPlaceholders(Main.translations.get("error.unsupported"),"","")), true);
                    return true;
                }
                return true;
        }
        return false;
    }
}
