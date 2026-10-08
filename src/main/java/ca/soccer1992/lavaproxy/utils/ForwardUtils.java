package ca.soccer1992.lavaproxy.utils;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Player;
import ca.soccer1992.lavaproxy.types.GameProperty;
import com.google.gson.JsonArray;
import io.netty.buffer.ByteBuf;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static ca.soccer1992.lavaproxy.utils.EncryptionUtils.SHA256HMac;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class ForwardUtils {
  public static byte[] buildVelocityData(Player player, String token,
                                         ArrayList<GameProperty> properties, int forwardType)
          throws NoSuchAlgorithmException, InvalidKeyException {

    if (forwardType != 1 && forwardType != 4)
      throw new IllegalArgumentException("Invalid forwardType");

    Connection con = player.con;
    byte[] signed;
    ByteBuf buf = con.nChannel.alloc().heapBuffer();
    try {
      writeVarInt(forwardType, buf);
      writeString(con.addr.getHostString(), buf);
      writeUUID(player.uuid, buf);
      writeString(player.name, buf);
      writePropertyArray(buf, con.protocol, properties);
      signed = new byte[buf.readableBytes()];
      buf.readBytes(signed);
    } finally {
      buf.release();
    }
    byte[] signature = SHA256HMac(signed, token);
    byte[] out = new byte[signature.length + signed.length];
    System.arraycopy(signature, 0, out, 0, signature.length);
    System.arraycopy(signed, 0, out, signature.length, signed.length);
    return out;
  }
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