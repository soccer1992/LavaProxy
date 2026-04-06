package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.ItemTagArray;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class UpdateTags extends Packet {
    public ArrayList<ItemTagArray> ItemTags;
    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "UpdateTags";

    public void decode (ByteBuf buf, MinecraftVersions proto){
        int amount = readVarInt(buf);
        ItemTags = new ArrayList<>();
        for (int i=0; i < amount; i ++){
            String reg = readString(buf);
            ItemTags.add(ItemTagArray.read(reg, buf));
        }
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeVarInt(ItemTags.size(),buf);
        for (ItemTagArray i : ItemTags){
            i.write(buf);
        }

    }
}
