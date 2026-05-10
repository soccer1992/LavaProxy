package ca.soccer1992.lavaproxy.packets.server;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class PluginResponse extends Packet {
    public byte[] data;
    public int messageID;

    public ConnectionTypes getType() { return ConnectionTypes.LOGIN; }
    public String name = "PluginResponse";
    public void setData(byte[] data){this.data = data;}
    public void setMessageID(int messageID){this.messageID = messageID;}
    public void decode (ByteBuf buf, MinecraftVersions proto){
        setMessageID(readVarInt(buf));
        setData(null);
        if (buf.readBoolean()) {
            byte[] tmp = new byte[buf.readableBytes()];
            buf.readBytes(tmp);
            setData(tmp);
        }
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeVarInt(messageID, buf);
        buf.writeBoolean(data != null);
        if (data != null){
            buf.writeBytes(data);
        }
    }
}
