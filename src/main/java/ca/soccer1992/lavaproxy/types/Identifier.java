package ca.soccer1992.lavaproxy.types;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readString;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public record Identifier(String id, String name) {
    public void write(ByteBuf buf, MinecraftVersions proto){
        writeString(id + ":" + name, buf);
    }
    public static Identifier read(ByteBuf buf, MinecraftVersions proto){
        String[] z = readString(buf).split(":");
        if (z.length > 2){
            throw new IllegalArgumentException("Identifier is invalid!");
        }
        if (z.length == 1){
            return new Identifier("minecraft", z[0]);
        }
        return new Identifier(z[0], z[1]);
    }
}