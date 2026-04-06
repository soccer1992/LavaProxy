package ca.soccer1992.lavaproxy.nbt;

import org.json.JSONArray;

import java.io.DataOutput;
import java.io.IOException;

public abstract class Tag {
    private final String name;
    protected Tag(String name) { this.name = name; }
    public String getName() { return name; }
    public abstract byte getType();
    public void write(DataOutput out) throws IOException {
        out.writeByte(getType());     // 1 byte tag ID
        out.writeUTF(getName());      // 2 bytes length + UTF-8 name
        writePayload(out);            // payload depends on tag type
    }
    public abstract void writePayload(DataOutput out) throws IOException;
    public abstract Object getJSON() throws IOException;

}