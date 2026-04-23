package ca.soccer1992.lavaproxy.types;

import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readString;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public record KnownPack(String ns, String ID, String Ver) {
    public static KnownPack read(ByteBuf buf){
        return new KnownPack(readString(buf),readString(buf),readString(buf));
    }
    public void write(ByteBuf buf){
        writeString(ns,buf);
        writeString(ID,buf);
        writeString(Ver,buf);
    }
}
