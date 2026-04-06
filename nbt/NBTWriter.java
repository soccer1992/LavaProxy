package ca.soccer1992.lavaproxy.nbt;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class NBTWriter {
    public static void write(CompoundTag root, OutputStream os, boolean gzip, boolean removeName) throws IOException {
        OutputStream out = gzip ? new GZIPOutputStream(os) : os;
        DataOutputStream data = new DataOutputStream(out);
        if (!removeName) {
            root.write(data);
        } else {
            data.writeByte(root.getType());

            root.writePayload(data);
        }
        data.flush();
        if (gzip) out.close();
    }
}