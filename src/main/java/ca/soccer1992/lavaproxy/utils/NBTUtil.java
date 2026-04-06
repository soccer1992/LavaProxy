package ca.soccer1992.lavaproxy.utils;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
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
    public static NamedTag streamModern(ByteBuf buf){

        try {
            ByteBuf combined = Unpooled.wrappedBuffer(
                    buf.readSlice(1),  // the type byte
                    Unpooled.wrappedBuffer(new byte[]{0x00, 0x00}), // empty name length
                    buf  // rest of the payload
            );
            NamedTag t = streamLegacy(combined);
            t.setName(null);
            return t;

        } catch (Exception e) {
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
