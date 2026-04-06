package ca.soccer1992.lavaproxy.utils;

import net.querz.io.Serializer;
import net.querz.nbt.io.NBTOutput;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.Tag;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class RewrittenSerializer implements Serializer<NamedTag> {

    private boolean compressed, littleEndian;

    public RewrittenSerializer() {
        this(true);
    }

    public RewrittenSerializer(boolean compressed) {
        this.compressed = compressed;
    }

    public RewrittenSerializer(boolean compressed, boolean littleEndian) {
        this.compressed = compressed;
        this.littleEndian = littleEndian;
    }

    @Override
    public void toStream(NamedTag object, OutputStream out) throws IOException {
        NBTOutput nbtOut;
        OutputStream output;
        if (compressed) {
            output = new GZIPOutputStream(out, true);
        } else {
            output = out;
        }

        if (littleEndian) {
            nbtOut = new RewrittenLEOutputStream(output);
        } else {
            nbtOut = new RewrittenNBTOutputStream(output);
        }
        nbtOut.writeTag(object, Tag.DEFAULT_MAX_DEPTH);
        nbtOut.flush();
    }
}