package ca.soccer1992.lavaproxy.nbt;

import org.json.JSONObject;

import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class CompoundTag extends Tag {
    public final Map<String, Tag> value = new LinkedHashMap<>();
    public CompoundTag(String name) { super(name); }
    public void put(Tag tag) { value.put(tag.getName(), tag); }
    public Tag get(String key) { return value.get(key); }
    public Collection<Tag> getAll() { return value.values(); }
    @Override public byte getType() { return TagType.COMPOUND.getId(); }
    @Override
    public void writePayload(DataOutput out) throws IOException {
        for (Tag tag : value.values()) {
            tag.write(out); // compound elements still include type + name
        }
        out.writeByte(TagType.END.getId());
    }
    public JSONObject getJSON() throws IOException {
        JSONObject comp = new JSONObject();

        for (Tag tag : value.values()){
            comp.put(tag.getName(), tag.getJSON());
        }
        return comp;
    }
}