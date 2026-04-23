package ca.soccer1992.lavaproxy.types;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import io.netty.buffer.ByteBuf;

public record Position(int x, int y, int z) {
    public void write(ByteBuf buf){
        long val = (((long) x & 0x3FFFFFF) << 38) | (((long) z & 0x3FFFFFF) << 12) | ((long) y & 0xFFF);
        buf.writeLong(val);
    }
    public static Position read(ByteBuf buf, MinecraftVersions proto){
        long val;
        val = buf.readLong();

        int x1 = (int)(val >> 38);
        int y1 = (int)(val << 52 >> 52);
        int z1 = (int)(val << 26 >> 38);
        if (proto.isLess(MinecraftVersions.MINECRAFT_1_14)) {
            x1 = (int)(val >>> 38);
            y1 = (int)(val << 52 >>> 52);
            z1 = (int)(val << 26 >>> 38);
        }
        return new Position(x1, y1, z1);
    }
}