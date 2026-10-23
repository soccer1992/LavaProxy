package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.eventhandlers.PluginMessageEvent;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.clientserver.*;
import ca.soccer1992.lavaproxy.packets.server.ClientInfo;
import ca.soccer1992.lavaproxy.types.Identifier;

public class ConfigHandler extends Handler{

    public boolean handle(Packet p, Connection c) {
        //System.out.println(p.getClass());
        //System.out.println(c.isClosed);
        //System.out.println(c.hasDisconnected);
        //System.out.println(c.protoReader);
//
        //System.out.println("CLIENT");
        if (p instanceof ClientInfo packet) {
            c.plr.setInfo(packet);
            if (c.backendConnection == null) return true;
            if (c.backendConnection.conType == ConnectionTypes.CONFIG) c.backendConnection.writePacket(packet);
            //c.connect(c.tryIter.next());
            //c.disconnect(ComponentUtils.parser.deserialize("<rainbow>Testing (CONFIGURATION)</rainbow>"),false);

            return true;
        }
        if (p instanceof FinishConfiguration){
            if (c.conType != ConnectionTypes.POST_CONFIG){
                c.disconnect("ConfigurationFinish sent before server ConfigurationFinish", true);
                return true;
            }
            // players
            if (!Main.players.containsKey(c.plr.uuid)){
                Main.players.put(c.plr.uuid, c.plr);
            }
            c.backendConnection.writePacket(new FinishConfiguration());
            PlayHandler.handlePlay(c, true);

            return true;
        }
        if (p instanceof KnownPacks packet){
            c.plr.knownPacks = packet.packs;
            c.backendConnection.writePacketServer(packet);
            return true;
        }
        if (p instanceof KeepAlive packet){
            if (c.keepAliveList.remove(packet.id) != null) return true;
            if (c.backendConnection != null){

                //System.out.println(c.backendConnection.protoReader + "keepalive");
                c.backendConnection.writePacketServer(packet);
            }
            return true;
        }
        if (p instanceof PluginMessage packet){
            PluginMessageEvent event = new PluginMessageEvent();
            event.channel = Identifier.read(packet.channel);
            event.fromBackend = false;
            event.data = packet.data;
            event.originatingPacket = packet;
            event.cancel = false;
            event.player = c.plr;
            for (ca.soccer1992.lavaproxy.eventhandlers.Handler handler : Main.eventHandlers){
                handler.handle(event);
                if (event.cancel) return true;
            }
            if (c.backendConnection == null) return true;
            if (c.backendConnection.conType == ConnectionTypes.CONFIG) c.backendConnection.writePacketServer(event.originatingPacket);

            return true;
        }
        return false;
    }
}
