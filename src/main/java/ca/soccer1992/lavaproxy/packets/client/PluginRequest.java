package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class PluginRequest extends Packet {
    public String channel;
    public byte[] data;
    public int messageID;

    public ConnectionTypes getType() { return ConnectionTypes.LOGIN; }
    public String name = "PluginRequest";
    public void setData(byte[] data){this.data = data;}
    public void setMessageID(int messageID){this.messageID = messageID;}

    public void setChannel(String channel){this.channel = channel;}
    public void decode (ByteBuf buf, MinecraftVersions proto){
        setMessageID(readVarInt(buf));
        setChannel(readString(buf));
        byte[] tmp = new byte[buf.readableBytes()];
        buf.readBytes(tmp);
        setData(tmp);
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeVarInt(messageID, buf);
        writeString(channel, buf);
        buf.writeBytes(data);
    }
}
