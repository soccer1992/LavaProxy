package ca.soccer1992.lavaproxy.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public record ItemTagArray(String registry, ArrayList<ItemTag> itemIDS) {
    public static ItemTagArray read(String registry, ByteBuf buf){


        int elements = readVarInt(buf);
        ArrayList<ItemTag> ids = new ArrayList<>();
        for (int i=0; i < elements; i++){
            ids.add(ItemTag.read(buf));
        }
        return new ItemTagArray(registry,ids);
    }
    public void write(ByteBuf buf){
        writeString(registry, buf);
        writeVarInt(itemIDS.size(),buf);
        for (ItemTag i : itemIDS){
            i.write(buf);
        }
        
    }
}
