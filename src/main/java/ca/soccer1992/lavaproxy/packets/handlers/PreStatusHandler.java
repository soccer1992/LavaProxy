package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.nbt.*;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.status.*;
import ca.soccer1992.lavaproxy.packets.readers.StatusReader;
import ca.soccer1992.lavaproxy.packets.server.status.*;

public class PreStatusHandler extends Handler{
    public boolean handle(Packet p, Connection c){
        if (p instanceof PingRequest packet){
            c.writePacketServer(packet);
            return true;
        }
        if (p instanceof StatusRequest){
            StatusResponse response = new StatusResponse();
            CompoundTag info = new CompoundTag("");
            CompoundTag ver = new CompoundTag("version");
            ver.put(new StringTag("name","LavaProxy v1.0"));
            ver.put(new IntTag("protocol", c.protocol.getProtocol()));
            info.put(ver);
            CompoundTag players = new CompoundTag("players");
            players.put(new IntTag("max",Integer.MAX_VALUE));
            players.put(new IntTag("online",Main.CON_AMOUNT));
            info.put(players);
            CompoundTag desc = new CompoundTag("description");
            desc.put(new StringTag("text","A LavaProxy proxy.\nTotal connections: " + Main.CON_AMOUNT));
            info.put(desc);
            try {
                response.setJSON(info.getJSON().toString());
            } catch (Exception e){
                c.close();
            }
            if (Main.logPings) System.out.println(c.fillPlaceholders(Main.translations.get("log.ping"), "", ""));

            c.conType = ConnectionTypes.STATUS;
            c.setReader(new StatusReader());
            c.setHandler(new StatusHandler());
            c.writePacket(response);

            return true;
        }
        return false;
    }
}
