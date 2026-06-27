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
import ca.soccer1992.lavaproxy.utils.NBTUtil;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.kyori.adventure.text.Component;

public class PreStatusHandler extends Handler{
    public boolean handle(Packet p, Connection c){
        if (p instanceof PingRequest packet){
            c.writePacketServer(packet);
            return true;
        }
        if (p instanceof StatusRequest){
            StatusResponse response = new StatusResponse();
            CompoundBinaryTag.Builder info = CompoundBinaryTag.builder();
            CompoundBinaryTag.Builder ver = CompoundBinaryTag.builder();
            ver.putString("name", "LavaProxy v1.0");
            int protocol = c.protocol.getProtocol();
            ver.putInt("protocol", protocol);
            info.put("version", ver.build());
            CompoundBinaryTag.Builder players = CompoundBinaryTag.builder();
            players.putInt("max", Integer.MAX_VALUE);
            players.putInt("online", Main.CON_AMOUNT);
            info.put("players", players.build());
            CompoundBinaryTag.Builder desc = CompoundBinaryTag.builder();
            if (c.protocol != MinecraftVersions.UNSUPPORTED) {
                Component comp = ComponentUtils.parser.deserialize(c.fillPlaceholders(Main.motd, "", ""));
                BinaryTag tag = ComponentUtils.nbt(comp, c.protocol);
                if (tag instanceof StringBinaryTag t){
                    desc = desc.put("text",t);
                } else if (tag instanceof CompoundBinaryTag t){
                    desc = desc.put(t);
                }
                //desc = desc.put((CompoundBinaryTag) );
                //desc.putString("text", "A LavaProxy proxy.\nTotal connections: " + Main.CON_AMOUNT);
            } else {
                Component comp = ComponentUtils.parser.deserialize(c.fillPlaceholders(Main.translations.get("error.unsupported"), "", ""));
                desc = desc.put((CompoundBinaryTag) ComponentUtils.nbt(comp, c.protocol));
            }
            info.put("description", desc.build());
            try {
                response.setJSON(NBTUtil.deserialize(info.build(), false).toString());
            } catch (Exception e) {
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
