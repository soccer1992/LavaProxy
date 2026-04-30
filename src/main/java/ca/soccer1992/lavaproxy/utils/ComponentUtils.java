package ca.soccer1992.lavaproxy.utils;



import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.*;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.CompoundTag;
public class ComponentUtils {
    public static MiniMessage parser = MiniMessage.miniMessage();
    public static String json(Component comp) {
        return JSONComponentSerializer.json().serialize(comp);
    }
    public static String plain(Component comp){
        return PlainTextComponentSerializer.plainText().serialize(comp);
    }
    public static Component fromJSON(String json){
        return JSONComponentSerializer.json().deserialize(json);
    }
    public static String miniMessage(Component comp){
        return parser.serialize(comp);
    }
    public static Component fromNBT(CompoundTag tag) {
        return fromJSON(tag.toString());
    }
    public static CompoundTag nbt(Component comp) {
        String json = json(comp);

        // Ensure it's a JSON object
        com.google.gson.JsonObject obj;
        try {
            com.google.gson.JsonElement el = com.google.gson.JsonParser.parseString(json);
            if (el.isJsonObject()) {
                obj = el.getAsJsonObject();
            } else {
                // wrap primitives/arrays
                obj = new com.google.gson.JsonObject();
                obj.add("text", new com.google.gson.JsonPrimitive(""));
                obj.add("extra", el.isJsonArray() ? el : com.google.gson.JsonParser.parseString("[" + json + "]"));
            }
        } catch (Exception e) {
            obj = new com.google.gson.JsonObject();
            obj.addProperty("text", json);
        }

        return jsonObjectToCompound(obj);
    }
    public static String compoundToJson(CompoundTag tag) {
        com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
        for (String key : tag.keySet()) {
            net.querz.nbt.tag.Tag<?> val = tag.get(key);
            if (val instanceof CompoundTag) {
                obj.add(key, com.google.gson.JsonParser.parseString(compoundToJson((CompoundTag) val)));
            } else if (val instanceof net.querz.nbt.tag.StringTag) {
                obj.addProperty(key, ((net.querz.nbt.tag.StringTag) val).getValue());
            } else if (val instanceof net.querz.nbt.tag.IntTag) {
                obj.addProperty(key, ((net.querz.nbt.tag.IntTag) val).asInt());
            } else if (val instanceof net.querz.nbt.tag.LongTag) {
                obj.addProperty(key, ((net.querz.nbt.tag.LongTag) val).asLong());
            }
        }
        return obj.toString();
    }

    private static CompoundTag jsonObjectToCompound(com.google.gson.JsonObject obj) {
        CompoundTag tag = new CompoundTag();
        for (var entry : obj.entrySet()) {
            com.google.gson.JsonElement val = entry.getValue();
            if (val.isJsonPrimitive()) {
                com.google.gson.JsonPrimitive prim = val.getAsJsonPrimitive();
                if (prim.isString()) tag.putString(entry.getKey(), prim.getAsString());
                else if (prim.isBoolean()) tag.putBoolean(entry.getKey(), prim.getAsBoolean());
                else if (prim.isNumber()) tag.putInt(entry.getKey(), prim.getAsInt());
            } else if (val.isJsonObject()) {
                tag.put(entry.getKey(), jsonObjectToCompound(val.getAsJsonObject()));
            } else if (val.isJsonArray()) {
                ListTag<CompoundTag> list = new ListTag<>(CompoundTag.class);
                for (com.google.gson.JsonElement el : val.getAsJsonArray()) {
                    if (el.isJsonObject()) {
                        list.add(jsonObjectToCompound(el.getAsJsonObject()));
                    } else if (el.isJsonPrimitive()) {
                        // string elements in extra array - wrap them
                        CompoundTag wrapped = new CompoundTag();
                        wrapped.putString("text", el.getAsString());
                        list.add(wrapped);
                    }
                }
                tag.put(entry.getKey(), list);
            }
        }
        return tag;
    }

}
