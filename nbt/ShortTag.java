package ca.soccer1992.lavaproxy.nbt;

import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends Tag {
    private final short value;
    public ShortTag(String name, short value) { super(name); this.value = value; }
    public short getValue() { return value; }
    @Override public byte getType() { return TagType.SHORT.getId(); }
    @Override
    public void writePayload(DataOutput out) throws IOException {
        out.writeShort(value);
    }
    @Override
    public Object getJSON(){
        return value;
    }
}