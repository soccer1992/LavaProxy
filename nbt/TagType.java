package ca.soccer1992.lavaproxy.nbt;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum TagType {

    END(0) {
        @Override
        Tag read(String name, DataInput in) {
            return null; // never directly read
        }
    },

    BYTE(1) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            return new ByteTag(name, in.readByte());
        }
    },

    SHORT(2) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            return new ShortTag(name, in.readShort());
        }
    },

    INT(3) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            return new IntTag(name, in.readInt());
        }
    },

    LONG(4) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            return new LongTag(name, in.readLong());
        }
    },

    FLOAT(5) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            return new FloatTag(name, in.readFloat());
        }
    },

    DOUBLE(6) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            return new DoubleTag(name, in.readDouble());
        }
    },

    BYTE_ARRAY(7) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            int length = in.readInt();
            byte[] data = new byte[length];
            in.readFully(data);
            return new ByteArrayTag(name, data);
        }
    },

    STRING(8) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            return new StringTag(name, in.readUTF());
        }
    },

    LIST(9) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            TagType elementType = fromId(in.readByte());
            int size = in.readInt();
            ListTag list = new ListTag(name, elementType);

            for (int i = 0; i < size; i++) {
                list.add(elementType.read("", in)); // list elements unnamed
            }

            return list;
        }
    },

    COMPOUND(10) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            CompoundTag compound = new CompoundTag(name);

            while (true) {
                TagType type = fromId(in.readUnsignedByte());
                if (type == END) break;

                String childName = in.readUTF();
                compound.put(type.read(childName, in));
            }

            return compound;
        }
    },

    INT_ARRAY(11) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            int length = in.readInt();
            int[] data = new int[length];
            for (int i = 0; i < length; i++) {
                data[i] = in.readInt();
            }
            return new IntArrayTag(name, data);
        }
    },

    LONG_ARRAY(12) {
        @Override
        Tag read(String name, DataInput in) throws IOException {
            int length = in.readInt();
            long[] data = new long[length];
            for (int i = 0; i < length; i++) {
                data[i] = in.readLong();
            }
            return new LongArrayTag(name, data);
        }
    };

    private final int id;

    TagType(int id) {
        this.id = id;
    }

    public byte getId() {
        return (byte) id;
    }

    abstract Tag read(String name, DataInput in) throws IOException;

    private static final Map<Integer, TagType> BY_ID = new HashMap<>();

    static {
        for (TagType type : values()) {
            BY_ID.put(type.id, type);
        }
    }

    public static TagType fromId(int id) {
        TagType type = BY_ID.get(id);
        if (type == null)
            throw new IllegalArgumentException("Invalid tag id: " + id);
        return type;
    }
}