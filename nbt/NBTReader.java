package ca.soccer1992.lavaproxy.nbt;

import io.netty.buffer.ByteBuf;

import java.io.*;
import java.util.HexFormat;
import java.util.zip.GZIPInputStream;

public class NBTReader {
    public static CompoundTag read(InputStream is, boolean gzip) throws IOException {
        InputStream in = gzip ? new GZIPInputStream(is) : is;
        DataInputStream data = new DataInputStream(in);
        TagType rootType = TagType.fromId(data.readUnsignedByte());
        if (rootType != TagType.COMPOUND) throw new IOException("Root tag must be a compound");
        String rootName = data.readUTF();
        return (CompoundTag) rootType.read(rootName, data);
    }
    public static CompoundTag read(ByteBuf buf) throws IOException {
        if (buf.readableBytes() == 0) return null;
        byte[] dta = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), dta);
        System.out.println(HexFormat.of().formatHex(dta));
        InputStream in = new ByteArrayInputStream(dta);
        if (dta.length >= 2 && dta[0] == (byte) 0x1f && dta[1] == (byte) 0x8b){
            return read(in, true);
        }
        System.out.println("nozip");
        return read(in, false);
    }
}