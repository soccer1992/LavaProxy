package ca.soccer1992.lavaproxy.packets.clientserver;

import ca.soccer1992.lavaproxy.MinecraftVersions;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class PluginMessage extends Packet {
    public String channel;
    public byte[] data;
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "PluginMessage";
    public void setData(byte[] data){this.data = data;}

    public void setChannel(String channel){this.channel = channel;}
    public void decode (ByteBuf buf, MinecraftVersions proto){
        setChannel(readString(buf));
        byte[] tmp = new byte[buf.readableBytes()];
        buf.readBytes(tmp);
        setData(tmp);
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(channel, buf);
        buf.writeBytes(data);
    }
}
