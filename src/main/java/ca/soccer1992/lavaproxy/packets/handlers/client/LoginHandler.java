package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.login.CompressionPacket;
import ca.soccer1992.lavaproxy.packets.client.login.LoginKick;
import ca.soccer1992.lavaproxy.packets.client.login.LoginSuccess;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
import ca.soccer1992.lavaproxy.packets.readers.ConfigReader;
import ca.soccer1992.lavaproxy.packets.server.ClientInfo;
import ca.soccer1992.lavaproxy.packets.server.LoginAck;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public class LoginHandler extends Handler {
    public boolean handle(Packet p, Connection c){

        if (p instanceof final CompressionPacket packet) {

            c.setCompression(packet.threshold);
            return true;
        }
        if (p instanceof LoginKick packet){
            String reason = packet.reason;

            c.backendConnection.backendDisconnect(reason);

            return true;
        }

        if (p instanceof LoginSuccess){
            c.writePacketServer(new LoginAck());

            if (c.protocol.getProtocol()<MinecraftVersions.MINECRAFT_1_20_2.getProtocol()){
                c.conType = ConnectionTypes.PLAY;
            }

            if (c.protocol.getProtocol()>= MinecraftVersions.MINECRAFT_1_20_2.getProtocol()) {
                c.conType = ConnectionTypes.CONFIG;
                c.setReader(new ConfigReader());
                c.setHandler(new ConfigHandler());
                PluginMessage brandMessage = new PluginMessage();
                brandMessage.setChannel("minecraft:brand");
                ByteBuf tmpOut = Unpooled.buffer();
                writeString(c.backendConnection.plr.brand,tmpOut);
                byte[] dta = new byte[tmpOut.readableBytes()];
                tmpOut.readBytes(dta);
                tmpOut.release();
                brandMessage.setData(dta);
                c.writePacketServer(brandMessage);
                ClientInfo info = c.plr.infoPacket;
                c.writePacketServer(info);

            }

            return true;
        }

        return false;
    }
}
