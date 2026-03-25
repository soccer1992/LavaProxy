package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.PluginMessage;
import ca.soccer1992.lavaproxy.packets.server.ClientInfo;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class ConfigHandler extends Handler{
    public boolean handle(Packet p, Connection c) {
        if (p instanceof ClientInfo) {
            c.disconnect(ComponentUtils.parser.deserialize("<rainbow>Testing (CONFIGURATION)</rainbow>"),false);

            return true;
        }
        if (p instanceof PluginMessage packet){
            ByteBuf dataBuf = Unpooled.copiedBuffer(packet.data);
            if (packet.channel.equals("minecraft:brand")){
                c.plr.setBrand(readString(dataBuf));
                System.out.printf("%s brand: %s%n",c.plr, c.plr.brand);
            }
            dataBuf.release();
            return true;
        }
        return false;
    }
}
