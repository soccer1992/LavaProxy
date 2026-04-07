package ca.soccer1992.lavaproxy.packets.clientserver;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;


public class KeepAlive extends Packet {
    public long id;
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "KeepAlive";

    public void setID(long id){this.id = id;}
    public void decode (ByteBuf buf, MinecraftVersions proto){
        id = buf.readLong();
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        buf.writeLong(id);
    }
}