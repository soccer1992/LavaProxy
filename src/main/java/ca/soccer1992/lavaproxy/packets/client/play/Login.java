package ca.soccer1992.lavaproxy.packets.client.play;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.types.WorldPosition;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.ArrayList;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class Login extends Packet {
    public int entityID;
    public boolean hardcore;
    public ArrayList<String> dimensions = new ArrayList<>();
    public int maxPlayers;
    public int viewDist;
    public int simDist;
    public boolean reduceDebug;
    public boolean respawnScreen;
    public boolean limitedCrafting;
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
    public boolean enforcesSecureChat;
    public CompoundBinaryTag registry;
    public CompoundBinaryTag currentDimensionData;
    public boolean isOnlineMode;
    public ConnectionTypes getType() { return ConnectionTypes.PLAY; }
    public String name = "Login";
    public void decode (ByteBuf buf, MinecraftVersions proto){
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_2)){
            decode1202(buf, proto);
        } else if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16)) {
            decode116(buf, proto);
        }
    }

    public void decode116(ByteBuf buf, MinecraftVersions proto) {
        entityID = buf.readInt();

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16_2)) {
            hardcore = buf.readBoolean();
            gamemode = buf.readByte();
        } else {
            gamemode = buf.readByte();
            hardcore = (gamemode & 0x08) != 0;
            gamemode &= ~0x08;
        }
        oldGamemode = buf.readByte();

        int dimCount = readVarInt(buf);
        for (int i = 0; i < dimCount; i++) {
            dimensions.add(readString(buf));
        }

        registry = (CompoundBinaryTag) readTag(buf, proto);

        boolean isBetween1162And119 = proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16_2)
                && proto.isLess(MinecraftVersions.MINECRAFT_1_19);

        if (isBetween1162And119) {
            currentDimensionData = (CompoundBinaryTag) readTag(buf, proto);
            dimension = readString(buf);
        } else {
            dimension = readString(buf);
            levelName = readString(buf);
        }

        seedHash = new byte[8];
        buf.readBytes(seedHash);

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16_2)) {
            maxPlayers = readVarInt(buf);
        } else {
            maxPlayers = buf.readUnsignedByte();
        }

        viewDist = readVarInt(buf);
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_18)) {
            simDist = readVarInt(buf);
        }

        reduceDebug = buf.readBoolean();
        respawnScreen = buf.readBoolean();

        debugWorld = buf.readBoolean();
        flatWorld = buf.readBoolean();

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_19) && buf.readBoolean()) {
            deathPos = WorldPosition.read(buf, proto);
        }

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20)) {
            portalCooldown = readVarInt(buf);
        }
    }

    public void encode116(ByteBuf buf, MinecraftVersions proto) {
        buf.writeInt(entityID);

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16_2)) {
            buf.writeBoolean(hardcore);
            buf.writeByte(gamemode);
        } else {
            buf.writeByte(hardcore ? (gamemode | 0x08) : gamemode);
        }
        buf.writeByte(oldGamemode);

        writeVarInt(dimensions.size(), buf);
        dimensions.forEach(d -> writeString(d, buf));

        writeTag(buf, proto, registry);

        boolean isBetween1162And119 = proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16_2)
                && proto.isLess(MinecraftVersions.MINECRAFT_1_19);

        if (isBetween1162And119) {
            writeTag(buf, proto, currentDimensionData);
            writeString(dimension, buf);
        } else {
            writeString(dimension, buf);
            writeString(levelName, buf);
        }

        buf.writeBytes(seedHash);

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16_2)) {
            writeVarInt(maxPlayers, buf);
        } else {
            buf.writeByte(maxPlayers);
        }

        writeVarInt(viewDist, buf);
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_18)) {
            writeVarInt(simDist, buf);
        }

        buf.writeBoolean(reduceDebug);
        buf.writeBoolean(respawnScreen);

        buf.writeBoolean(debugWorld);
        buf.writeBoolean(flatWorld);

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_19)) {
            buf.writeBoolean(deathPos != null);
            if (deathPos != null) {
                deathPos.write(buf, proto);
            }
        }

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20)) {
            writeVarInt(portalCooldown, buf);
        }
    }

    public void encode1202(ByteBuf buf, MinecraftVersions proto){
        buf.writeInt(entityID);
        buf.writeBoolean(hardcore);
        writeVarInt(dimensions.size(), buf);
        dimensions.forEach((String ele) -> writeString(ele,buf));
        writeVarInt(maxPlayers, buf);
        writeVarInt(viewDist,buf);
        writeVarInt(simDist,buf);
        buf.writeBoolean(reduceDebug);
        buf.writeBoolean(respawnScreen);
        buf.writeBoolean(limitedCrafting);

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_5)){
            writeVarInt(dimensionID, buf);
        } else {
            writeString(dimension,buf);
        }
        writeString(levelName,buf);
        //levelName = readString(buf);
        //seedHash = new byte[8];
        buf.writeBytes(seedHash);
        //buf.readBytes(seedHash);
        //gamemode = buf.readByte();
        //oldGamemode = buf.readByte();
        buf.writeByte(gamemode);
        buf.writeByte(oldGamemode);
        buf.writeBoolean(debugWorld);
        buf.writeBoolean(flatWorld);
        buf.writeBoolean(deathPos != null);

        if (deathPos != null) {
            deathPos.write(buf,proto);
        }
        writeVarInt(portalCooldown,buf);

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_21_2)) {
            writeVarInt(seaLevel,buf);
            //this.seaLevel = readVarInt(buf);
        }
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_26_2)){
            buf.writeBoolean(isOnlineMode);
        }
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_5)) {
            buf.writeBoolean(enforcesSecureChat);
        }

    }
    public void decode1202(ByteBuf buf, MinecraftVersions proto){
        entityID = buf.readInt();
        hardcore = buf.readBoolean();
        int dimCount = readVarInt(buf);
        for (int i=0;i<dimCount;i++){
            String dim = readString(buf);
            dimensions.add(dim);
        }
        maxPlayers = readVarInt(buf); // unused
        viewDist = readVarInt(buf);
        simDist = readVarInt(buf);
        reduceDebug = buf.readBoolean();
        respawnScreen = buf.readBoolean();
        limitedCrafting = buf.readBoolean(); // unused also LOL
        dimension = "";

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_5)){
            dimensionID = readVarInt(buf);
            dimension = dimensions.get(dimensionID);
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
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_26_2)){
            this.isOnlineMode = buf.readBoolean();
        }

        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_5)) {
            this.enforcesSecureChat = buf.readBoolean();
        }

    }

    public void encode(ByteBuf buf, MinecraftVersions proto){
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_2)){
            encode1202(buf, proto);
        } else if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_16)) {
            encode116(buf, proto);
        }

    }
}