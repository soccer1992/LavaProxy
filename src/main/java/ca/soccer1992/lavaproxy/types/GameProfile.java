package ca.soccer1992.lavaproxy.types;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.utils.PacketHelpers;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.UUID;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class GameProfile {
  public String name;
  public UUID uuid;
  public ArrayList<GameProperty> properties;

  public void write(ByteBuf buf, MinecraftVersions proto){
    writeUUID(uuid, buf);

    writeString(name, buf);
    writePropertyArray(buf, proto, properties);
  }
  public static GameProfile read(ByteBuf buf, MinecraftVersions proto){
    return new GameProfile(readUUID(buf), readString(buf), PacketHelpers.readPropertyArray(buf, proto));
  }
  public GameProfile(UUID uuid, String name, ArrayList<GameProperty> properties){
    if (properties == null) properties = new ArrayList<>();
    this.name = name;
    this.uuid = uuid;
    this.properties = properties;
  }

}
