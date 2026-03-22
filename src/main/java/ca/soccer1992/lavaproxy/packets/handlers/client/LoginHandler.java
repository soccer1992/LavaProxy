package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.CompressionPacket;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
public class LoginHandler extends Handler {
    public boolean handle(Packet p, Connection c){

        if (p instanceof final CompressionPacket packet) {
            c.setCompression(packet.threshold);
            return true;
        }


        return false;
    }
}
