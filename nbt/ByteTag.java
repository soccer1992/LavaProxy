package ca.soccer1992.lavaproxy.nbt;

import org.json.JSONArray;

import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends Tag {
    private final int value;
    public ByteTag(String name, int value) { super(name); this.value = value; }
    public int getValue() { return value; }
    @Override public byte getType() { return TagType.BYTE.getId(); }
    @Override
    public void writePayload(DataOutput out) throws IOException {
        out.writeByte(value);
    }
    @Override
    public Object getJSON(){
        return value;
    }
}