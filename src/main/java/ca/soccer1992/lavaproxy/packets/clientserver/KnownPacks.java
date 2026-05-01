package ca.soccer1992.lavaproxy.packets.clientserver;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.types.KnownPack;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readVarInt;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeVarInt;

public class KnownPacks extends Packet {
    public ArrayList<KnownPack> packs;
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "KnownPacks";

    public void decode (ByteBuf buf, MinecraftVersions proto){
        int amount = readVarInt(buf);
        packs = new ArrayList<>();
        for (int i=0; i < amount; i ++){
            packs.add(KnownPack.read(buf));
        }

    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeVarInt(packs.size(),buf);
        for (KnownPack i : packs){
            i.write(buf);
        }

    }
}
