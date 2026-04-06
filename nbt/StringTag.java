package ca.soccer1992.lavaproxy.nbt;

import java.io.DataOutput;
import java.io.IOException;

public class StringTag extends Tag {
    private final String value;
    public StringTag(String name, String value) { super(name); this.value = value; }
    public String getValue() { return value; }
    @Override public byte getType() { return TagType.STRING.getId(); }

    @Override
    public void writePayload(DataOutput out) throws IOException {
        out.writeUTF(value);
    }
    @Override
    public Object getJSON(){
        return value;
    }
}