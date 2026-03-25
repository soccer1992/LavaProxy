package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.NBTKick;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.fromJSON;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readString;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public class ConfigHandler extends Handler {
    public boolean handle(Packet p, Connection c){
        if (p instanceof final NBTKick packet){
            try {
                c.backendConnection.backendDisconnect(fromJSON(packet.reason.getJSON().toString()));
            } catch (Exception e){
                c.backendConnection.disconnect(e.toString(), true);
            }
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
                System.out.println(rewritten);
                c.backendConnection.writePacket(rewrite);
                buf.release();
            }
            dataBuf.release();
            return true;
        }
        System.out.println(p);

        return false;
    }
}
