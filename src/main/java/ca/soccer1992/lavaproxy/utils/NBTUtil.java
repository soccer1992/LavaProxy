package ca.soccer1992.lavaproxy.utils;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.Tag;

import java.io.IOException;
import java.io.OutputStream;

public class NBTUtil {
    public static void convertLegacy(Tag t, OutputStream stream){
        try {
            new RewrittenSerializer(false).toStream(new NamedTag("", t), stream);
        } catch (IOException ignored){
        }

    }
    public static NamedTag streamLegacy(ByteBuf buf){
        try {
            return new NBTDeserializer(false).fromStream(new ByteBufInputStream(buf));
        } catch (Exception e){
            return null;
        }

    }
    public static NamedTag streamModern(ByteBuf buf) {
        try {
            byte type = buf.readByte();
            int remaining = buf.readableBytes();
            byte[] data = new byte[remaining];
            buf.getBytes(buf.readerIndex(), data); // peek

            // add type, (null-length) and wrap in a counting stream
            byte[] full = new byte[3 + remaining];
            full[0] = type;
            full[1] = 0x00;
            full[2] = 0x00;
            System.arraycopy(data, 0, full, 3, remaining);

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(full);
            NamedTag t = new NBTDeserializer(false).fromStream(bais);

            // bais.available() tells us how many bytes are LEFT unread
            int consumed = full.length - bais.available();
            // subtract the 3 header bytes injected
            int fromBuf = consumed - 3;

            buf.readerIndex(buf.readerIndex() + fromBuf); // advance buf correctly

            if (t != null) t.setName(null);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static NamedTag streamAuto(ByteBuf buf, MinecraftVersions ver){
        if (ver.getProtocol()>=MinecraftVersions.MINECRAFT_1_20_2.getProtocol()) return streamModern(buf);
        return streamLegacy(buf);
    }
    public static String snbt(Tag tag){
        try {
            return SNBTUtil.toSNBT(tag);
        } catch (IOException e){
            return null;
        }
    }
    public static void convertAuto(Tag t, OutputStream stream, MinecraftVersions ver){
        if (ver.getProtocol()>=MinecraftVersions.MINECRAFT_1_20_2.getProtocol()){
            convertModern(t,stream);
        } else {
            convertLegacy(t,stream);
        }
    }
    public static void convertModern(Tag t, OutputStream stream){
        try {
            new RewrittenSerializer(false).toStream(new NamedTag(null, t), stream);
        } catch (IOException ignored){
        }

    }
}
