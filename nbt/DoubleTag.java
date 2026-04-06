package ca.soccer1992.lavaproxy.nbt;

import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag extends Tag {
    private final double value;
    public DoubleTag(String name, double value) { super(name); this.value = value; }
    public double getValue() { return value; }
    @Override public byte getType() { return TagType.DOUBLE.getId(); }
    @Override
    public void writePayload(DataOutput out) throws IOException {
        out.writeDouble(value);
    }
    @Override
    public Object getJSON(){
        return value;
    }
}