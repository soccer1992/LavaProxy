package ca.soccer1992.lavaproxy.packets;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import io.netty.buffer.ByteBuf;

public class InvalidPacket extends Packet {
    public int id;
    public byte[] data;

    public ConnectionTypes getType() { return ConnectionTypes.LOGIN; }
    public String name = "InvalidPacket";


    public void decode (ByteBuf buf, MinecraftVersions proto){

        data = new byte[buf.readableBytes()];
        buf.readBytes(data);
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        buf.writeBytes(data);

    }

}
