package ca.soccer1992.lavaproxy.packets.server;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class FeatureFlags extends Packet {
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "FeatureFlags";
    public ArrayList<String> enabled_features;

    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeVarInt(enabled_features.size(), buf);
        for (String i : enabled_features){
            writeString(i, buf);
        }

    }
    public void decode(ByteBuf buf, MinecraftVersions proto){
        int length = readVarInt(buf);
        enabled_features = new ArrayList<>();
        for (int i=0; i < length; i++){
            enabled_features.add(readString(buf));
        }

    }
}
