package ca.soccer1992.lavaproxy.nbt;

import org.json.JSONArray;

import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class ListTag extends Tag {
    private final TagType elementType;
    private final List<Tag> elements = new ArrayList<>();
    public ListTag(String name, TagType elementType) { super(name); this.elementType = elementType; }
    public void add(Tag tag) { elements.add(tag); }
    public List<Tag> getElements() { return elements; }
    @Override public byte getType() { return TagType.LIST.getId(); }

    @Override
    public void writePayload(DataOutput out) throws IOException {
        // Only payload, no type or name (used if this list is nested inside another list)
        out.writeByte(elementType.getId());
        out.writeInt(elements.size());
        for (Tag tag : elements) {
            tag.writePayload(out);
        }
    }
    @Override
    public Object getJSON() throws IOException {
        JSONArray arr = new JSONArray();
        for (Tag a : elements){
            arr.put(a.getJSON());
        }
        return arr;
    }
}