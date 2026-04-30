package ca.soccer1992.lavaproxy.types;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import net.querz.nbt.tag.CompoundTag;


import static ca.soccer1992.lavaproxy.utils.NBTUtil.convertAuto;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public record RegistryPart(String entry, CompoundTag nbt, int id, boolean hasNBT) {
    public void write(ByteBuf buf, MinecraftVersions proto){
        writeString(entry,buf);
        buf.writeBoolean(hasNBT);
        if (hasNBT){
            try {
                convertAuto(nbt, new ByteBufOutputStream(buf),proto);
            } catch (Exception ignored){
            }
        }

    }
}
