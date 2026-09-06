package ca.soccer1992.lavaproxy.types;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readString;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public record GameProperty(String key, String value, String sig) {
  public void write(ByteBuf buf, MinecraftVersions proto){
    writeString(key, buf);
    writeString(value, buf);
    buf.writeBoolean(sig != null);
    if (sig != null) writeString(sig, buf);
  }
  public static GameProperty read(ByteBuf buf, MinecraftVersions proto){

    return new GameProperty(readString(buf), readString(buf), buf.readBoolean() ? readString(buf) : null);
  }
  public JsonObject toJsonObject(){
    // Map the key to "name" and the value to "value"
    JsonObject propertyPair = new JsonObject();

    propertyPair.addProperty("name", key);
    propertyPair.addProperty("value", value);
    if (sig != null) propertyPair.addProperty("signature",sig);
    return propertyPair;
  }
}