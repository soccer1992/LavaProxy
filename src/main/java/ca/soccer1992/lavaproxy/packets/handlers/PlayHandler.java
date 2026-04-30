package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.readers.PlayReader;
import ca.soccer1992.lavaproxy.packets.server.ClientInfo;

import java.util.Arrays;

public class PlayHandler extends Handler{

    public static void handlePlay(Connection c){
        c.conType = ConnectionTypes.PLAY;
        c.setReader(new PlayReader());
        c.setHandler(new PlayHandler());

        c.backendConnection.setReader(new PlayReader());

        c.backendConnection.setHandler(new ca.soccer1992.lavaproxy.packets.handlers.client.PlayHandler());
        c.tryIter = Arrays.stream(Main.trys).iterator();
        if (Main.trys[0].equals(c.connectedServer)) c.tryIter.next();
        c._recentDisconnectMessage = null;
        //c.disconnect(ComponentUtils.parser.deserialize("<rainbow>Connected to " + c.connectedServer + "</rainbow>"), false);
    }
    public boolean handle(Packet p, Connection c){
        if (p instanceof ClientInfo packet) {
            c.plr.setInfo(packet);
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof PluginMessage packet) {
            c.backendConnection.writePacket(packet);
            return true;
        }
        return false;
    }
}
