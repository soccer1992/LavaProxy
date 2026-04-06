package ca.soccer1992.lavaproxy.nbt;

import org.json.JSONArray;

import java.io.DataOutput;
import java.io.IOException;

public class ByteArrayTag extends Tag {
    private final byte[] value;
    public ByteArrayTag(String name, byte[] value) { super(name); this.value = value; }
    public byte[] getValue() { return value; }
    @Override public byte getType() { return TagType.BYTE_ARRAY.getId(); }
    @Override
    public void writePayload(DataOutput out) throws IOException {
        // Only payload, no type or name (used if this list is nested inside another list)
        out.writeInt(value.length);
        for (byte l : value) out.writeByte(l);
    }
    @Override
    public Object getJSON(){
        JSONArray arr = new JSONArray();
        for (byte l : value) arr.put(l);
        return arr;
    }
}