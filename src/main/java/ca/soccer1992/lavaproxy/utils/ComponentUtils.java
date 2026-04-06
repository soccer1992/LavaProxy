package ca.soccer1992.lavaproxy.utils;



import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.*;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.io.SNBTUtil;
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
    public static CompoundTag nbt(Component comp){
        String json = json(comp);
        //CompoundTag out = new CompoundTag("");
        //Nbt NBT = new Nbt();
        if (json.startsWith("{")){
            try {
                CompoundTag parsed = (CompoundTag) SNBTUtil.fromSNBT(json);
                return parsed;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        } else if (json.startsWith("[")){

            try {
                ListTag parsed = (ListTag) SNBTUtil.fromSNBT(json);
                CompoundTag out = new CompoundTag();
                out.put("extra",parsed);
                out.putString("text","");
                return out;
            } catch (Exception e){
                return null;
            }

        } else if ((json.startsWith("\"") && json.endsWith("\"")) || (json.startsWith("'") && json.endsWith("'"))){
            try {
                StringTag parsed = (StringTag) SNBTUtil.fromSNBT(json);
                CompoundTag out = new CompoundTag();
                out.put("text",parsed);
                return out;
            } catch (Exception e){
                return null;
            }

        } else {
            CompoundTag out = new CompoundTag();
            out.putString("text",json);
            return out;

        }
    }

}
