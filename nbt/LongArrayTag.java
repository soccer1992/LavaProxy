package ca.soccer1992.lavaproxy.nbt;

import org.json.JSONArray;

import java.io.DataOutput;
import java.io.IOException;

public class LongArrayTag extends Tag {
    private final long[] value;
    public LongArrayTag(String name, long[] value) { super(name); this.value = value; }
    public long[] getValue() { return value; }
    @Override public byte getType() { return TagType.LONG_ARRAY.getId(); }
    @Override
    public void writePayload(DataOutput out) throws IOException {
        // Only payload, no type or name (used if this list is nested inside another list)
        out.writeInt(value.length);
        for (long l : value) out.writeLong(l);
    }
    @Override
    public Object getJSON() throws IOException {
        JSONArray arr = new JSONArray();
        for (long a : value){
            arr.put(a);
        }
        return arr;
    }
}