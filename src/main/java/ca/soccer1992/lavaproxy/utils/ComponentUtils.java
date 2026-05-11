package ca.soccer1992.lavaproxy.utils;



import ca.soccer1992.lavaproxy.MinecraftVersions;
import com.google.gson.JsonElement;
import net.kyori.adventure.nbt.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.*;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import static ca.soccer1992.lavaproxy.utils.NBTUtil.deserialize;
import static ca.soccer1992.lavaproxy.utils.NBTUtil.serialize;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.serializerForVersion;


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
    public static Component fromNBT(BinaryTag tag) {
        return JSONComponentSerializer.json().deserialize(deserialize(tag, true).toString());
    }

    public static BinaryTag nbt(Component comp, MinecraftVersions ver){
        // convert to JSON
        JsonElement gson = serializerForVersion(ver).serializeToTree(comp);
        return serialize(gson);
    }


}
