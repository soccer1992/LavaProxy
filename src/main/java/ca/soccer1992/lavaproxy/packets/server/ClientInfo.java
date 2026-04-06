package ca.soccer1992.lavaproxy.packets.server;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;
public class ClientInfo extends Packet {
    public ConnectionTypes getType() { return ConnectionTypes.POST_SUCCESS; }
    public String name = "ClientInfo";
    public String locale;
    public byte viewDist;
    public int ChatMode;
    public boolean ColorsEnabled;
    public short skin;
    public int hand;
    public boolean textFiltering;
    public boolean serverList;
    public int particleStatus;
    public void encode(ByteBuf buf, MinecraftVersions proto) {
        writeString(locale, buf); // lang
        buf.writeByte(viewDist);
        writeVarInt(ChatMode, buf); // 0 = enabled, 1 = commands only, 2 = hidden
        buf.writeBoolean(ColorsEnabled); // show chat colors
        buf.writeByte(skin); // bit mask
        writeVarInt(hand, buf); // 0 = left, 1 = right
        if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_17.getProtocol()) return;
        buf.writeBoolean(textFiltering);
        if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_18.getProtocol()) return;
        buf.writeBoolean(serverList); // show in player list
        if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_21_2.getProtocol()) return;
        writeVarInt(particleStatus, buf);



    }
    public void decode(ByteBuf buf, MinecraftVersions proto){
        locale = readString(buf);
        viewDist = buf.readByte();
        ChatMode = readVarInt(buf);
        ColorsEnabled = buf.readBoolean();
        skin = buf.readUnsignedByte();
        hand = readVarInt(buf);
        if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_17.getProtocol()) return;
        textFiltering = buf.readBoolean();
        if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_18.getProtocol()) return;
        serverList = buf.readBoolean();
        if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_21_2.getProtocol()) return;
        particleStatus = readVarInt(buf);

    }
}