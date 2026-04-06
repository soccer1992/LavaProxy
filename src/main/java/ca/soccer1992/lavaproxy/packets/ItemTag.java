package ca.soccer1992.lavaproxy.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public record ItemTag(String name, ArrayList<Integer> itemIDS) {
    public static ItemTag read(ByteBuf buf){

        String n = readString(buf);
        int elements = readVarInt(buf);
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i=0; i < elements; i++){
            ids.add(readVarInt(buf));
        }
        return new ItemTag(n, ids);
    }
    public void write(ByteBuf buf){
        writeString(name, buf);
        writeVarInt(itemIDS.size(),buf);
        for (int i : itemIDS){
            writeVarInt(i, buf);
        }

    }
}
