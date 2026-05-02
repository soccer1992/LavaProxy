package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.status.*;
import ca.soccer1992.lavaproxy.packets.readers.StatusReader;
import ca.soccer1992.lavaproxy.packets.server.status.*;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.querz.nbt.tag.CompoundTag;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.compoundToJson;
import static ca.soccer1992.lavaproxy.utils.ComponentUtils.nbt;

public class PreStatusHandler extends Handler{
    public boolean handle(Packet p, Connection c){
        if (p instanceof PingRequest packet){
            c.writePacketServer(packet);
            return true;
        }
        if (p instanceof StatusRequest){
            StatusResponse response = new StatusResponse();
            CompoundTag info = new CompoundTag();
            CompoundTag ver = new CompoundTag();
            ver.putString("name","LavaProxy v1.0");
            int protocol = c.protocol.getProtocol();
            ver.putInt("protocol", protocol);
            info.put("version",ver);
            CompoundTag players = new CompoundTag();
            players.putInt("max",Integer.MAX_VALUE);
            players.putInt("online",Main.CON_AMOUNT);
            info.put("players",players);
            CompoundTag desc = new CompoundTag();
            if (c.protocol != MinecraftVersions.UNSUPPORTED) {
                desc.putString("text", "A LavaProxy proxy.\nTotal connections: " + Main.CON_AMOUNT);
            } else {
                Component comp = ComponentUtils.parser.deserialize(c.fillPlaceholders(Main.translations.get("error.unsupported"),"",""));
                desc = nbt(comp);
            }
            info.put("description",desc);
            try {
                response.setJSON(compoundToJson(info));
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
