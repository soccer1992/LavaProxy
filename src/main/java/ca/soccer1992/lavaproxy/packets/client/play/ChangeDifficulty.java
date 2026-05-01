package ca.soccer1992.lavaproxy.packets.client.play;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.types.Difficulty;
import io.netty.buffer.ByteBuf;


public class ChangeDifficulty extends Packet {
    public Difficulty diff;
    public boolean locked;
    public ConnectionTypes getType() { return ConnectionTypes.PLAY; }
    public String name = "ChangeDifficulty";
    public void decode (ByteBuf buf, MinecraftVersions proto){
        diff = Difficulty.fromValue(buf.readUnsignedByte());
        locked = buf.readBoolean();
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        buf.writeByte(diff.getValue());
        buf.writeBoolean(locked);
    }

}
