package ca.soccer1992.lavaproxy.nbt;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends Tag {
    private final int value;
    public IntTag(String name, int value) { super(name); this.value = value; }
    public int getValue() { return value; }
    @Override public byte getType() { return TagType.INT.getId(); }

    @Override
    public void writePayload(DataOutput out) throws IOException {
        out.writeInt(value);
    }
    @Override
    public Object getJSON(){
        return value;
    }
}