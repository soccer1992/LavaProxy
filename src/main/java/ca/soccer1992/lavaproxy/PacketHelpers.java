package ca.soccer1992.lavaproxy;

import io.netty.buffer.ByteBuf;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

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
    public static Object readWithoutEat(Function<ByteBuf,Object> readFunc, ByteBuf buf){
        ByteBuf clone = buf.copy();
        return readFunc.apply(clone);
    }
    public static byte[] decompress(final byte[] compressedBytes) throws IOException {
        if (compressedBytes == null || compressedBytes.length == 0) {
            return new byte[0];
        }

        // Use try-with-resources to ensure streams are closed automatically
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            // Read from the GZIP stream and write to the output stream
            while ((len = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }
    public static String readString(ByteBuf buf){
        int len = readVarInt(buf);
        //System.out.println(len);
        try {
            String thing = (String) buf.readCharSequence(len, StandardCharsets.UTF_8);
            return thing;

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
