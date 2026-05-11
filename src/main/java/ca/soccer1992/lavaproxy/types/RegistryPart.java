package ca.soccer1992.lavaproxy.types;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.nbt.BinaryTag;


import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public record RegistryPart(String entry, BinaryTag nbt, int id, boolean hasNBT) {
    public void write(ByteBuf buf, MinecraftVersions proto){
        writeString(entry,buf);
        buf.writeBoolean(hasNBT);
        if (hasNBT){
            writeTag(buf, proto, nbt);
        }

    }
}
