package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.Login;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;

public class PlayHandler extends Handler {

    public boolean handle(Packet p, Connection c) {
        if (p instanceof Login packet){
            c._dimensionName = packet.dimension;
            //c._dimInfo = c._dimensionCodec.getCompoundTag(packet.dimension);
            //System.out.println(c._dimInfo);
            c.backendConnection.backendDisconnect(ComponentUtils.parser.deserialize("<rainbow>Simulation distance: " + packet.simDist + "</rainbow><br>" + c._dimensionName));
            return true;
        }
        if (p instanceof PluginMessage packet) {
            c.backendConnection.writePacket(packet);
            return true;
        }
        return false;
    }
}
