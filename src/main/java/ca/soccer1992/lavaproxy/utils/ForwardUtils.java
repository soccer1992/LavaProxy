package ca.soccer1992.lavaproxy.utils;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class ForwardUtils {
  public static String buildBungeeCordData(Player player, JsonObject properties) {
    Connection con = player.con;

    String data = con.connectAddr.getHostString()
            + '\0'
            + con.addr.getHostString()
            + '\0'
            + player.uuid.toString().replace("-", "");

    if (properties != null) {
      data += '\0';
      JsonArray propertiesArray = new JsonArray();

      // 2. Loop through every key-value pair inside your x:y object
      for (Map.Entry<String, JsonElement> entry : properties.entrySet()) {
        JsonObject propertyPair = new JsonObject();

        // Map the key to "name" and the value to "value"
        propertyPair.addProperty("name", entry.getKey());
        propertyPair.addProperty("value", entry.getValue().getAsString());

        // Add this pair to the main array
        propertiesArray.add(propertyPair);
      }
      data += propertiesArray.toString();
    }

    return data;
  }
  public static String buildBungeeGuardData(Player player, String token, JsonObject properties) {
    JsonObject data = properties != null ? properties.deepCopy() : new JsonObject();
    data.addProperty("bungeeguard-token", token);

    return buildBungeeCordData(player, data);
  }
}