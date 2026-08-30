package ca.soccer1992.lavaproxy.utils;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Player;
import com.google.gson.JsonObject;

public class ForwardUtils {
  public static String buildBungeeCordData(Player player, JsonObject properties) {
    Connection con = player.con;

    String data = con.connectAddr.getHostString()
            + '\0'
            + con.addr.getHostString()
            + '\0'
            + player.uuid.toString().replace("-", "");

    if (properties != null) data += '\0' + properties.toString();

    return data;
  }
  public static String buildBungeeGuardData(Player player, String token, JsonObject properties) {
    JsonObject data = properties != null ? properties.deepCopy() : new JsonObject();
    data.addProperty("bungeeguard-token", token);

    return buildBungeeCordData(player, data);
  }
}