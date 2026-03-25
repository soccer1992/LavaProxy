package ca.soccer1992.lavaproxy.utils;

import ca.soccer1992.lavaproxy.nbt.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class ComponentUtils {
    public static MiniMessage parser = MiniMessage.miniMessage();
    public static String json(Component comp) {
        return JSONComponentSerializer.json().serialize(comp);
    }
    public static CompoundTag nbt(Component comp){
        String json = json(comp);
        JSONObject out = new JSONObject();
        if (json.startsWith("{")){
            out = new JSONObject(json);
        } else if (json.startsWith("[")){
            out.put("extra",new JSONArray(json));
            out.put("text","");
        }
        return parse("", out);
    }
    public static CompoundTag parse(String name, JSONObject obj) {
        CompoundTag compound = new CompoundTag(name);

        for (String key : obj.keySet()) {
            Object value = obj.get(key);
            compound.put(parseValue(key, value));
        }

        return compound;
    }

    private static Tag parseValue(String name, Object value) {

        if (value instanceof JSONObject jsonObj) {
            return parse(name, jsonObj);
        }

        if (value instanceof JSONArray jsonArr) {
            return parseList(name, jsonArr);
        }

        if (value instanceof String str) {
            return new StringTag(name, str);
        }

        if (value instanceof Integer i) {
            return new IntTag(name, i);
        }

        if (value instanceof Long l) {
            return new LongTag(name, l);
        }

        if (value instanceof Double d) {
            return new DoubleTag(name, d);
        }

        if (value instanceof Float f) {
            return new FloatTag(name, f);
        }

        if (value instanceof Boolean b) {
            return new ByteTag(name, (byte) (b ? 1 : 0));
        }

        throw new IllegalArgumentException("Unsupported type: " + value.getClass());
    }
    private static ListTag parseList(String name, JSONArray arr) {
        if (arr.isEmpty()) {
            return new ListTag(name, TagType.END); // or handle empty case differently
        }

        Object first = arr.get(0);
        TagType type = getTagType(first);

        ListTag list = new ListTag(name, type);

        for (int i = 0; i < arr.length(); i++) {
            Object val = arr.get(i);
            list.add(parseValue("", val)); // lists have unnamed elements
        }

        return list;
    }
    private static TagType getTagType(Object value) {
        if (value instanceof JSONObject) return TagType.COMPOUND;
        if (value instanceof JSONArray) return TagType.LIST;
        if (value instanceof String) return TagType.STRING;
        if (value instanceof Integer) return TagType.INT;
        if (value instanceof Long) return TagType.LONG;
        if (value instanceof Double) return TagType.DOUBLE;
        if (value instanceof Float) return TagType.FLOAT;
        if (value instanceof Boolean) return TagType.BYTE;

        throw new IllegalArgumentException("Unknown list type: " + value.getClass());
    }
}
