package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.utils.PacketHelpers;
import io.netty.buffer.ByteBuf;

public class CompressionPacket extends Packet {
    public ConnectionTypes getType() { return ConnectionTypes.LOGIN; }

    public String name = "CompressionPacket";

    public int threshold;

    public void decode(ByteBuf buf, MinecraftVersions proto){
        this.threshold = PacketHelpers.readVarInt(buf);
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        PacketHelpers.writeVarInt(this.threshold, buf);
    }

}
