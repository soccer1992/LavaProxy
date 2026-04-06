package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.clientserver.FinishConfiguration;
import ca.soccer1992.lavaproxy.packets.clientserver.KnownPacks;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.server.ClientInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class ConfigHandler extends Handler{
    public boolean handle(Packet p, Connection c) {
        if (p instanceof ClientInfo packet) {
            c.plr.setInfo(packet);
            c.connect(c.tryIter.next());
            //c.disconnect(ComponentUtils.parser.deserialize("<rainbow>Testing (CONFIGURATION)</rainbow>"),false);

            return true;
        }
        if (p instanceof FinishConfiguration){
            System.out.println(c.conType);
            if (c.conType != ConnectionTypes.POST_CONFIG){
                c.disconnect("ConfigurationFinish sent before server ConfigurationFinish", true);
                return true;
            }
            System.out.println("SWITCHED TO PLAY");
            c.conType = ConnectionTypes.PLAY;
            c.backendConnection.writePacket(new FinishConfiguration());
            return true;
        }
        if (p instanceof KnownPacks packet){
            System.out.println("e");
            c.backendConnection.writePacketServer(packet);
            return true;
        }
        if (p instanceof PluginMessage packet){
            ByteBuf dataBuf = Unpooled.copiedBuffer(packet.data);
            if (packet.channel.equals("minecraft:brand")){
                c.plr.setBrand(readString(dataBuf));
                System.out.println(c.fillPlaceholders(Main.translations.get("log.brand"), "", c.plr.brand));
                //System.out.printf("%s brand: %s%n",c.plr, c.plr.brand);
            }
            dataBuf.release();
            return true;
        }
        return false;
    }
}
