package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.InvalidPacket;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.NBTKick;
import ca.soccer1992.lavaproxy.packets.client.UnsignedChat;
import ca.soccer1992.lavaproxy.packets.client.play.*;
import ca.soccer1992.lavaproxy.packets.clientserver.EnterConfiguration;
import ca.soccer1992.lavaproxy.packets.clientserver.KeepAlive;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.fromJSON;

public class PlayHandler extends Handler {

    public boolean handle(Packet p, Connection c) {
        if (c.backendConnection.waitingServer!=null) return true;
        //System.out.println("[OUT] " + p.getClass().getSimpleName());
        if (p instanceof final NBTKick packet){
            try {
                c.backendConnection.backendDisconnect(fromJSON(packet.reasonJSON().toString()));
            } catch (Exception e){
                c.backendConnection.disconnect(e.toString(), true);
            }
            return true;
        }
        if (p instanceof Login packet){
            c.backendConnection.lastServer = null;
            c._dimensionName = packet.dimension;
            c.backendConnection.writePacket(packet);
            System.out.println(c.backendConnection.fillPlaceholders("log.connected", "", c.backendConnection.plr.brand));
            //c.backendConnection.plr.sendMessage(Component.text("if you see this, it worked."),false);

            //c._dimInfo = c._dimensionCodec.getCompoundTag(packet.dimension);
            //System.out.println(c._dimInfo);
            //c.backendConnection.backendDisconnect(ComponentUtils.parser.deserialize("<rainbow>Simulation distance: " + packet.simDist + "</rainbow><br>" + c._dimensionName));
            return true;
        }
        if (p instanceof EnterConfiguration){
            c.backendConnection.writePacketServer(p);

            return true;
        }
        if (p instanceof UnsignedChat packet){
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof PluginMessage packet) {
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof SystemChat packet){
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
            if (c.keepAliveList.remove(packet.id) != null) return true;
            c.backendConnection.writePacket(packet);
            return true;
        }
        return false;
    }
}
