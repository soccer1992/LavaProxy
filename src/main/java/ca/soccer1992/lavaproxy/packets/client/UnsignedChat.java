package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class UnsignedChat extends Packet {
    public ConnectionTypes getType() { return ConnectionTypes.PLAY; }
    public String name = "UnsignedChat";
    public String msg;
    public byte type;
    public UUID sender;
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(msg,buf);
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_8)){
            buf.writeByte(type);
        }
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16)){
            writeUUID(sender, buf);
        }
    }
    public void decode(ByteBuf buf, MinecraftVersions proto){
        msg = readString(buf);
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_8)){
            type = buf.readByte();
        }
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16)){
            sender = readUUID(buf);
        }
    }
}
