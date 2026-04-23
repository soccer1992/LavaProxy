package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.Login;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;
public class PlayHandler extends Handler {

    public boolean handle(Packet p, Connection c) {
        if (p instanceof Login packet){
            c.backendConnection.backendDisconnect(ComponentUtils.parser.deserialize("<rainbow>Simulation distance: " + packet.simDist + "</rainbow>"));
            return true;
        }
        return false;
    }
}
