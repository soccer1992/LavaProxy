package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class RegistryData extends Packet {
    //public Map<Integer, RegistryPart> RegistryData;
    public byte[] IHateParsingNBTSoIAmJustGonnaStoreEverythingInHere;
    public String id;
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "UpdateTags";

    public void decode (ByteBuf buf, MinecraftVersions proto){
        id = readString(buf);
        IHateParsingNBTSoIAmJustGonnaStoreEverythingInHere = new byte[buf.readableBytes()];
        buf.readBytes(IHateParsingNBTSoIAmJustGonnaStoreEverythingInHere);
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(id,buf);
        buf.writeBytes(IHateParsingNBTSoIAmJustGonnaStoreEverythingInHere);

    }
}
