package ca.soccer1992.lavaproxy.types;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readString;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public record WorldPosition(String world, Position pos) {
    public void write(ByteBuf buf, MinecraftVersions proto){
        writeString(world,buf);
        pos.write(buf);
    }
    public static WorldPosition read(ByteBuf buf, MinecraftVersions proto){

        return new WorldPosition(readString(buf), Position.read(buf,proto));
    }
}