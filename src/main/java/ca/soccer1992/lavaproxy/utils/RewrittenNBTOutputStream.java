package ca.soccer1992.lavaproxy.utils;

import net.querz.io.ExceptionTriConsumer;
import net.querz.io.MaxDepthIO;
import net.querz.nbt.io.NBTOutput;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.EndTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class RewrittenNBTOutputStream extends DataOutputStream implements NBTOutput, MaxDepthIO {

    private static Map<Byte, ExceptionTriConsumer<RewrittenNBTOutputStream, Tag<?>, Integer, IOException>> writers = new HashMap<>();
    private static Map<Class<?>, Byte> classIdMapping = new HashMap<>();

    static {
        put(EndTag.ID, (o, t, d) -> {}, EndTag.class);
        put(ByteTag.ID, (o, t, d) -> writeByte(o, t), ByteTag.class);
        put(ShortTag.ID, (o, t, d) -> writeShort(o, t), ShortTag.class);
        put(IntTag.ID, (o, t, d) -> writeInt(o, t), IntTag.class);
        put(LongTag.ID, (o, t, d) -> writeLong(o, t), LongTag.class);
        put(FloatTag.ID, (o, t, d) -> writeFloat(o, t), FloatTag.class);
        put(DoubleTag.ID, (o, t, d) -> writeDouble(o, t), DoubleTag.class);
        put(ByteArrayTag.ID, (o, t, d) -> writeByteArray(o, t), ByteArrayTag.class);
        put(StringTag.ID, (o, t, d) -> writeString(o, t), StringTag.class);
        put(ListTag.ID, RewrittenNBTOutputStream::writeList, ListTag.class);
        put(CompoundTag.ID, RewrittenNBTOutputStream::writeCompound, CompoundTag.class);
        put(IntArrayTag.ID, (o, t, d) -> writeIntArray(o, t), IntArrayTag.class);
        put(LongArrayTag.ID, (o, t, d) -> writeLongArray(o, t), LongArrayTag.class);
    }

    private static void put(byte id, ExceptionTriConsumer<RewrittenNBTOutputStream, Tag<?>, Integer, IOException> f, Class<?> clazz) {
        writers.put(id, f);
        classIdMapping.put(clazz, id);
    }

    public RewrittenNBTOutputStream(OutputStream out) {
        super(out);
    }

    public void writeTag(NamedTag tag, int maxDepth) throws IOException {
        writeByte(tag.getTag().getID());
        if (tag.getTag().getID() != 0) {
            if (tag.getName() != null){
                writeUTF(tag.getName());

            }
        }
        writeRawTag(tag.getTag(), maxDepth);
    }

    public void writeTag(Tag<?> tag, int maxDepth) throws IOException {
        writeByte(tag.getID());
        if (tag.getID() != 0) {
            writeUTF("");
        }
        writeRawTag(tag, maxDepth);
    }

    public void writeRawTag(Tag<?> tag, int maxDepth) throws IOException {
        ExceptionTriConsumer<RewrittenNBTOutputStream, Tag<?>, Integer, IOException> f;
        if ((f = writers.get(tag.getID())) == null) {
            throw new IOException("invalid tag \"" + tag.getID() + "\"");
        }
        f.accept(this, tag, maxDepth);
    }

    static byte idFromClass(Class<?> clazz) {
        Byte id = classIdMapping.get(clazz);
        if (id == null) {
            throw new IllegalArgumentException("unknown Tag class " + clazz.getName());
        }
        return id;
    }

    private static void writeByte(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeByte(((ByteTag) tag).asByte());
    }

    private static void writeShort(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeShort(((ShortTag) tag).asShort());
    }

    private static void writeInt(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeInt(((IntTag) tag).asInt());
    }

    private static void writeLong(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeLong(((LongTag) tag).asLong());
    }

    private static void writeFloat(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeFloat(((FloatTag) tag).asFloat());
    }

    private static void writeDouble(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeDouble(((DoubleTag) tag).asDouble());
    }

    private static void writeString(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeUTF(((StringTag) tag).getValue());
    }

    private static void writeByteArray(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeInt(((ByteArrayTag) tag).length());
        out.write(((ByteArrayTag) tag).getValue());
    }

    private static void writeIntArray(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeInt(((IntArrayTag) tag).length());
        for (int i : ((IntArrayTag) tag).getValue()) {
            out.writeInt(i);
        }
    }

    private static void writeLongArray(RewrittenNBTOutputStream out, Tag<?> tag) throws IOException {
        out.writeInt(((LongArrayTag) tag).length());
        for (long l : ((LongArrayTag) tag).getValue()) {
            out.writeLong(l);
        }
    }

    private static void writeList(RewrittenNBTOutputStream out, Tag<?> tag, int maxDepth) throws IOException {
        out.writeByte(idFromClass(((ListTag<?>) tag).getTypeClass()));
        out.writeInt(((ListTag<?>) tag).size());
        for (Tag<?> t : ((ListTag<?>) tag)) {
            out.writeRawTag(t, out.decrementMaxDepth(maxDepth));
        }
    }

    private static void writeCompound(RewrittenNBTOutputStream out, Tag<?> tag, int maxDepth) throws IOException {
        for (Map.Entry<String, Tag<?>> entry : (CompoundTag) tag) {
            if (entry.getValue().getID() == 0) {
                throw new IOException("end tag not allowed");
            }
            out.writeByte(entry.getValue().getID());
            out.writeUTF(entry.getKey());
            out.writeRawTag(entry.getValue(), out.decrementMaxDepth(maxDepth));
        }
        out.writeByte(0);
    }
}