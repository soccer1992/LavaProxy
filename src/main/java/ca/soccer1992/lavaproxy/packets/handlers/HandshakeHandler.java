package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.HandshakeIntent;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.readers.LoginReader;
import ca.soccer1992.lavaproxy.packets.readers.PreStatusReader;
import ca.soccer1992.lavaproxy.packets.server.HandshakePacket;

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
                return true;
        }
        return false;
    }
}
