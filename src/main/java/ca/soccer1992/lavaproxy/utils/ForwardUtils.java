package ca.soccer1992.lavaproxy.utils;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Player;
import ca.soccer1992.lavaproxy.types.GameProperty;
import com.google.gson.JsonArray;

import java.util.ArrayList;

public class ForwardUtils {
  public static String buildBungeeCordData(Player player, ArrayList<GameProperty> properties) {
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
      for (GameProperty prop : properties) propertiesArray.add(prop.toJsonObject());

      data += propertiesArray.toString();
    }

    return data;
  }
  public static String buildBungeeGuardData(Player player, String token, ArrayList<GameProperty> properties) {
    ArrayList<GameProperty> realProp = new ArrayList<>();
    if (properties != null) realProp.addAll(properties);
    realProp.add(new GameProperty("bungeeguard-token",token, null));
    return buildBungeeCordData(player, realProp);
  }
}