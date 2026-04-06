package ca.soccer1992.lavaproxy.nbt;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends Tag {
    private final long value;
    public LongTag(String name, long value) { super(name); this.value = value; }
    public long getValue() { return value; }
    @Override public byte getType() { return TagType.LONG.getId(); }
    @Override
    public void writePayload(DataOutput out) throws IOException {
        out.writeLong(value);
    }
    @Override
    public Object getJSON(){
        return value;
    }
}