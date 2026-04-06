package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.NBTKick;
import ca.soccer1992.lavaproxy.packets.client.RegistryData;
import ca.soccer1992.lavaproxy.packets.client.Transfer;
import ca.soccer1992.lavaproxy.packets.client.UpdateTags;
import ca.soccer1992.lavaproxy.packets.clientserver.FinishConfiguration;
import ca.soccer1992.lavaproxy.packets.clientserver.KeepAlive;
import ca.soccer1992.lavaproxy.packets.clientserver.KnownPacks;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
import ca.soccer1992.lavaproxy.packets.server.FeatureFlags;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.fromJSON;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readString;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public class ConfigHandler extends Handler {
    public boolean handle(Packet p, Connection c){
        if (p instanceof final NBTKick packet){
            try {
                System.out.println(packet.reason);
                c.backendConnection.backendDisconnect(fromJSON(packet.reasonJSON()));
            } catch (Exception e){
                c.backendConnection.disconnect(e.toString(), true);
            }
            return true;
        }
        if (p instanceof final Transfer packet){
            System.out.println(c.backendConnection.fillPlaceholders("backend.transfer", "", "", packet.host, packet.port));
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof final RegistryData packet){
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof final KeepAlive packet){

            System.out.println("Received KeepAlive");
            c.writePacket(packet);
            return true;
        }
        if (p instanceof FeatureFlags packet){
            c.plr.enabled_features = packet.enabled_features;
            c.backendConnection.plr.enabled_features = packet.enabled_features;
            return true;
        }
        if (p instanceof KnownPacks packet){
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof FinishConfiguration){
            c.backendConnection.conType = ConnectionTypes.POST_CONFIG;
            System.out.println(c.backendConnection.conType);
            System.out.println("e");
            c.backendConnection.writePacket(new FinishConfiguration());
            c.conType = ConnectionTypes.PLAY;
            return true;
        }
        if (p instanceof final UpdateTags packet){
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof final PluginMessage packet) {
            ByteBuf dataBuf = Unpooled.copiedBuffer(packet.data);

            if (packet.channel.equalsIgnoreCase("minecraft:brand")){
                // rewrite
                PluginMessage rewrite = new PluginMessage();
                String rewritten = readString(dataBuf);
                rewritten = String.format("%s [%s]",rewritten,"LavaProxy");
                ByteBuf buf = Unpooled.buffer();
                writeString(rewritten, buf);
                byte[] out = new byte[buf.readableBytes()];
                buf.readBytes(out);
                rewrite.setChannel(packet.channel);
                rewrite.setData(out);
                c.backendConnection.writePacket(rewrite);
                buf.release();
            }
            dataBuf.release();
            return true;
        }

        return false;
    }
}
