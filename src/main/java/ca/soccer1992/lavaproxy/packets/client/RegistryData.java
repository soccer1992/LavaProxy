package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.types.Identifier;
import ca.soccer1992.lavaproxy.types.RegistryPart;
import ca.soccer1992.lavaproxy.utils.NBTUtil;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;

import java.util.HashMap;
import java.util.Map;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;
public class RegistryData extends Packet {
    public Map<Identifier,Map<Integer, RegistryPart>> RegistryData;
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "RegistryData";

    public void decode (ByteBuf buf, MinecraftVersions proto){
        RegistryData = new HashMap<>();
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_5)) {
            Identifier id = Identifier.read(buf, proto);
            Map<Integer, RegistryPart> RegistryMappings = new HashMap<>();

            int length = readVarInt(buf);
            for (int i = 0; i < length; i++) {
                String name = readString(buf);
                BinaryTag nbt = buf.readBoolean() ? readTag(buf, proto) : null;
                RegistryMappings.put(i, new RegistryPart(name, nbt, i, nbt != null));
            }
            RegistryData.put(id, RegistryMappings);
        } else {
            BinaryTag nbt = readTag(buf, proto);
            if (!(nbt instanceof CompoundBinaryTag data)){
                throw new IllegalArgumentException("Tag in RegistryData is not a Compound tag!");
            }
            for (String name : data.keySet()){
                CompoundBinaryTag registry = data.getCompound(name);
                Identifier type = Identifier.read(registry.getString("type"));
                Map<Integer, RegistryPart> RegistryMappings = new HashMap<>();
                for (BinaryTag tag : registry.getList("value")){
                    if (!(tag instanceof CompoundBinaryTag entry)){
                        throw new IllegalArgumentException("Tag in RegistryData is not a Compound tag!");
                    }
                    int id = entry.getInt("id");
                    RegistryMappings.put(id, new RegistryPart(entry.getString("name"), entry, id, true));
                }
                RegistryData.put(type, RegistryMappings);
            }
        }
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_5)) {
            for (Map.Entry<Identifier, Map<Integer, RegistryPart>> mapping : RegistryData.entrySet()) {
                mapping.getKey().write(buf, proto);
                writeVarInt(mapping.getValue().values().size(), buf);
                for (RegistryPart part : mapping.getValue().values()) {
                    part.write(buf, proto);
                }
            }
        } else {
            CompoundBinaryTag.Builder combined = CompoundBinaryTag.builder();
            for (Map.Entry<Identifier, Map<Integer, RegistryPart>> mapping : RegistryData.entrySet()) {
                CompoundBinaryTag.Builder inner = CompoundBinaryTag.builder();

                ListBinaryTag.Builder data = ListBinaryTag.builder();

                for (RegistryPart part : mapping.getValue().values()) {
                    CompoundBinaryTag.Builder tmp = CompoundBinaryTag.builder().put((CompoundBinaryTag) part.nbt());
                    tmp.putString("name",part.entry());
                    tmp.putInt("id",part.id());
                    data.add((BinaryTag) tmp.build());
                }
                inner.putString("type",mapping.getKey().toString());
                inner.put("value",data.build());
                combined.put(mapping.getKey().toString(), inner.build());
            }
            writeTag(buf, proto, combined.build());
        }


    }
}
