package ca.soccer1992.lavaproxy.nbt;

import java.io.DataOutput;
import java.io.IOException;

public class FloatTag extends Tag {
    private final float value;
    public FloatTag(String name, float value) { super(name); this.value = value; }
    public float getValue() { return value; }
    @Override public byte getType() { return TagType.FLOAT.getId(); }
    @Override
    public void writePayload(DataOutput out) throws IOException {
        out.writeFloat(value);
    }
    @Override
    public Object getJSON(){
        return value;
    }
}