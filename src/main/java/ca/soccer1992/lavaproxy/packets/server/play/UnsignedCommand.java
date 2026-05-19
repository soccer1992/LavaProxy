package ca.soccer1992.lavaproxy.packets.server.play;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readString;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public class UnsignedCommand extends Packet {
    public ConnectionTypes getType() { return ConnectionTypes.PLAY; }
    public String name = "UnsignedCommand";
    public String msg;

    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(msg,buf);

    }
    public void decode(ByteBuf buf, MinecraftVersions proto){
        msg = readString(buf);

    }
}
