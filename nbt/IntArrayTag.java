package ca.soccer1992.lavaproxy.nbt;

import java.io.DataOutput;
import java.io.IOException;

public class IntArrayTag extends Tag {
    private final int[] value;
    public IntArrayTag(String name, int[] value) { super(name); this.value = value; }
    public int[] getValue() { return value; }
    @Override public byte getType() { return TagType.INT_ARRAY.getId(); }

    @Override
    public void writePayload(DataOutput out) throws IOException {
        // Only payload, no type or name (used if this list is nested inside another list)
        out.writeInt(value.length);
        for (int l : value) out.writeInt(l);
    }
    @Override
    public Object getJSON(){
        return value;
    }
}
