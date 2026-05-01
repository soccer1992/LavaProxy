package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.InvalidPacket;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.play.*;
import ca.soccer1992.lavaproxy.packets.clientserver.KeepAlive;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
public class PlayHandler extends Handler {

    public boolean handle(Packet p, Connection c) {
        if (p instanceof Login packet){
            c._dimensionName = packet.dimension;
            c.backendConnection.writePacket(packet);
            //c._dimInfo = c._dimensionCodec.getCompoundTag(packet.dimension);
            //System.out.println(c._dimInfo);
            //c.backendConnection.backendDisconnect(ComponentUtils.parser.deserialize("<rainbow>Simulation distance: " + packet.simDist + "</rainbow><br>" + c._dimensionName));
            return true;
        }
        if (p instanceof PluginMessage packet) {
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof BundleDelimiter packet) {
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof InvalidPacket packet ){
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof KeepAlive packet) {
            c.backendConnection.writePacket(packet);
            return true;
        }
        return false;
    }
}
