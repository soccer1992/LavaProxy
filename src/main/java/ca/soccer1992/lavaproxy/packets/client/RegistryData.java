package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.types.Identifier;
import ca.soccer1992.lavaproxy.types.RegistryPart;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.nbt.BinaryTag;

import java.util.HashMap;
import java.util.Map;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;
public class RegistryData extends Packet {
    public Map<Integer, RegistryPart> RegistryData;
    public Identifier id;
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "RegistryData";

    public void decode (ByteBuf buf, MinecraftVersions proto){
        RegistryData = new HashMap<>();
        id = Identifier.read(buf, proto);
        int length = readVarInt(buf);
        for (int i=0; i< length ; i++){
            String name =  readString(buf);
            BinaryTag nbt = buf.readBoolean() ? readTag(buf, proto) : null;
            RegistryData.put(i, new RegistryPart(name, nbt, i, nbt != null));
        }
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        id.write(buf, proto);
        writeVarInt(RegistryData.values().size(), buf);
        for (RegistryPart part : RegistryData.values()){
            part.write(buf, proto);
        }

    }
}
