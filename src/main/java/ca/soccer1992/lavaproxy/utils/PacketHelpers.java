package ca.soccer1992.lavaproxy.utils;

import io.netty.buffer.ByteBuf;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
public class PacketHelpers {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;
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
