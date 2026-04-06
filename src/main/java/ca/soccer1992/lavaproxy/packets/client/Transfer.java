package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;


public class Transfer extends Packet {
    public String host;
    public int port;
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "Transfer";
    public void setPort(int port){this.port = port;}

    public void setHost(String host){this.host = host;}

    public void decode (ByteBuf buf, MinecraftVersions proto){
        setHost(readString(buf));
        setPort(readVarInt(buf));
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(host, buf);
        writeVarInt(port, buf);
    }

}
