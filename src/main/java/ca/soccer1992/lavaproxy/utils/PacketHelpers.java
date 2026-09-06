package ca.soccer1992.lavaproxy.utils;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.types.GameProperty;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.kyori.option.OptionSchema;
import net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
public class PacketHelpers {
    private static final GsonComponentSerializer PRE_1_16_SERIALIZER =
            GsonComponentSerializer.builder()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // before 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.VALUE_FIELD)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    // before 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.FALSE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer PRE_1_20_3_SERIALIZER =
            GsonComponentSerializer.builder()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, true)
                                    // before 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.FALSE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer PRE_1_21_5_SERIALIZER =
            GsonComponentSerializer.builder()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, true)
                                    // after 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.TRUE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.TRUE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer MODERN_SERIALIZER =
            GsonComponentSerializer.builder()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.SNAKE_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.SNAKE_CASE)
                                    // after 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.TRUE)
                                    // after 1.21.5
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.FALSE)
                                    .build()
                    )
                    .build();

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;
    public static final int MAX_PACKET_SIZE = 2 * 1024 * 1024;
    public static final int MAX_BUF_SIZE = 2 * 1024 * 1024;

    @SuppressWarnings("unchecked")
    private static final BinaryTagType<? extends BinaryTag>[] BINARY_TAG_TYPES = new BinaryTagType[] {
            BinaryTagTypes.END, BinaryTagTypes.BYTE, BinaryTagTypes.SHORT, BinaryTagTypes.INT,
            BinaryTagTypes.LONG, BinaryTagTypes.FLOAT, BinaryTagTypes.DOUBLE,
            BinaryTagTypes.BYTE_ARRAY, BinaryTagTypes.STRING, BinaryTagTypes.LIST,
            BinaryTagTypes.COMPOUND, BinaryTagTypes.INT_ARRAY, BinaryTagTypes.LONG_ARRAY};
    public static GsonComponentSerializer serializerForVersion(MinecraftVersions ver){
        if (ver.isGreaterEquals(MinecraftVersions.MINECRAFT_1_21_5)) return MODERN_SERIALIZER;
        if (ver.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_3)) return PRE_1_21_5_SERIALIZER;
        if (ver.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16)) return PRE_1_20_3_SERIALIZER;
        return PRE_1_16_SERIALIZER;
    }
    public static ArrayList<GameProperty> readPropertyArray(ByteBuf buf, MinecraftVersions ver){
        ArrayList<GameProperty> properties = new ArrayList<>();
        int length = readVarInt(buf);
        for (int i=0;i<length;i++) properties.add(GameProperty.read(buf, ver));
        return properties;
    }
    public static void writePropertyArray(ByteBuf buf, MinecraftVersions ver, ArrayList<GameProperty> properties){
        writeVarInt(properties.size(), buf);
        for (GameProperty i : properties) i.write(buf, ver);
    }
    public static BinaryTag readTag(ByteBuf buf, MinecraftVersions ver){
        BinaryTagType<? extends BinaryTag> type = BINARY_TAG_TYPES[buf.readByte()];

        if (ver.isLess(MinecraftVersions.MINECRAFT_1_20_2)){
            // skip name data
            buf.skipBytes(buf.readUnsignedShort());
        }
        // We can read NBT as a 1.20.2+ NBT, even on <1.20.2!
        try{
            return type.read(new ByteBufInputStream(buf));
        } catch (IOException exception){
            throw new DecoderException("Failed reading NBT: " + exception.getMessage());
        }
    }
    public static void writeTag(ByteBuf buf, MinecraftVersions ver, BinaryTag tag){
        BinaryTagType type = tag.type();
        buf.writeByte(type.id());
        try {
            if (ver.isLess(MinecraftVersions.MINECRAFT_1_20_2)) {
                // skip name data
                buf.writeShort(0); // unsigned short (no-name)
            }
            type.write(tag, new ByteBufOutputStream(buf));
        } catch (IOException e){
            throw new EncoderException("Failed encoding NBT: " + e.getMessage());
        }

    }
    public static long readVarLong(ByteBuf buf) {
        int read = buf.readableBytes();
        if (read == 0) {
            throw new RuntimeException("Invalid ByteBuf (readVarLong)");
        }
        long value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = buf.readByte();
            value |= (long) (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 64) throw new RuntimeException("VarLong is too big");
        }

        return value;
    }

    public static int readVarInt(ByteBuf buf) {
        int read = buf.readableBytes();
        if (read == 0) {
            throw new RuntimeException("Invalid ByteBuf (readVarInt)");
        }
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = buf.readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) return 0;
        }
        //System.out.println(value);
        return value;
    }
    public static void writeVarInt(int value, ByteBuf os) {

        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                os.writeByte(value);
                return;
            }

            os.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);

            // Note: >>> means that the leftmost bits are filled with zeroes regardless of the sign,
            // rather than being filled with copies of the sign bit to preserve the sign.
            // In languages that don't have a ">>>" operator, This behavior can often be selected by
            // performing the shift on an unsigned type.
            value >>>= 7;
        }
    }
    public static void writeString(String str, ByteBuf buf) {
        writeVarInt(str.length(), buf);
        buf.writeCharSequence(str, StandardCharsets.UTF_8);

    }
    public static byte[] compress(byte[] data) {
        try {
            Deflater deflater = new Deflater();
            deflater.setInput(data);
            deflater.finish();

            byte[] buffer = new byte[data.length];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                bos.write(buffer, 0, count);
            }

            deflater.end();
            return bos.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] decompress(byte[] compressed, int expectedSize) {
        try {

            Inflater inflater = new Inflater();
            inflater.setInput(compressed);

            ByteArrayOutputStream bos = new ByteArrayOutputStream(expectedSize > 0 ? expectedSize : compressed.length);
            byte[] buffer = new byte[1024];

            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);

                if (count == 0) {
                    if (inflater.needsInput() || inflater.needsDictionary()) {
                        break;
                    }
                }

                bos.write(buffer, 0, count);
            }

            inflater.end();
            return bos.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }
    public static String readString(ByteBuf buf){
        int len = readVarInt(buf);
        //System.out.println(len);
        try {
            return (String) buf.readCharSequence(len, StandardCharsets.UTF_8);

        } catch (Exception e){
            return "";
        }


    }
    public static void writeUUID(UUID uuid, ByteBuf buf){
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf buf){
        return new UUID(buf.readLong(), buf.readLong());

    }
    public static void writeFixedBitSet(ByteBuf buf, boolean[] bits, int n) {
        byte[] bytes = new byte[(n + 7) / 8];
        for (int i = 0; i < n; i++) {
            if (bits[i]) bytes[i / 8] |= (byte) (1 << (i % 8));
        }
        buf.writeBytes(bytes);
    }
    public static boolean[] readFixedBitSet(ByteBuf buf, int n) {
        byte[] bytes = new byte[(n + 7) / 8];
        buf.readBytes(bytes);
        boolean[] bits = new boolean[n];
        for (int i = 0; i < n; i++) {
            bits[i] = (bytes[i / 8] & (1 << (i % 8))) != 0;
        }
        return bits;
    }
    public static void writeVarLong(long value, ByteBuf os) {

        while (true) {
            if ((value & ~((long) SEGMENT_BITS)) == 0) {
                os.writeByte((int) value);
                return;
            }

            os.writeByte((int) ((value & SEGMENT_BITS) | CONTINUE_BIT));

            // Note: >>> means that the leftmost bits are filled with zeroes regardless of the sign,
            // rather than being filled with copies of the sign bit to preserve the sign.
            // In languages that don't have a ">>>" operator, This behavior can often be selected by
            // performing the shift on an unsigned type.
            value >>>= 7;
        }
    }
}
