package ca.soccer1992.lavaproxy.packets.client.play;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.types.WorldPosition;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class Respawn extends Packet {
  public String dimension;
  public int dimensionID;
  public String levelName;
  public byte[] seedHash;
  public byte gamemode;
  public byte oldGamemode;
  public boolean debugWorld;
  public boolean flatWorld;
  public WorldPosition deathPos = null;
  public int portalCooldown;
  public int seaLevel;
  public byte dataToKeep;

  public ConnectionTypes getType() { return ConnectionTypes.PLAY; }
  public String name = "Respawn";

  public void decode(ByteBuf buf, MinecraftVersions proto){
    if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_2)){
      decode1202(buf, proto);
    }
  }

  public void encode(ByteBuf buf, MinecraftVersions proto){
    if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_2)){
      encode1202(buf, proto);
    }
  }

  public void decode1202(ByteBuf buf, MinecraftVersions proto){
    if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_5)){
      dimensionID = readVarInt(buf);
    } else {
      dimension = readString(buf);
    }
    levelName = readString(buf);
    seedHash = new byte[8];
    buf.readBytes(seedHash);

    gamemode = buf.readByte();
    oldGamemode = buf.readByte();
    debugWorld = buf.readBoolean();
    flatWorld = buf.readBoolean();

    if (buf.readBoolean()) {
      this.deathPos = WorldPosition.read(buf,proto);
    }

    this.portalCooldown = readVarInt(buf);

    if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_21_2)) {
      this.seaLevel = readVarInt(buf);
    }

    this.dataToKeep = buf.readByte();
  }

  public void encode1202(ByteBuf buf, MinecraftVersions proto){
    if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_5)){
      writeVarInt(dimensionID, buf);
    } else {
      writeString(dimension, buf);
    }
    writeString(levelName, buf);
    buf.writeBytes(seedHash);

    buf.writeByte(gamemode);
    buf.writeByte(oldGamemode);
    buf.writeBoolean(debugWorld);
    buf.writeBoolean(flatWorld);
    buf.writeBoolean(deathPos != null);

    if (deathPos != null) {
      deathPos.write(buf,proto);
    }

    writeVarInt(portalCooldown, buf);

    if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_21_2)) {
      writeVarInt(seaLevel, buf);
    }

    buf.writeByte(dataToKeep);
  }
}