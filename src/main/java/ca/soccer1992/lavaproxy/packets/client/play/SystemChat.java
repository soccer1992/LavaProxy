package ca.soccer1992.lavaproxy.packets.client.play;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.text.Component;


import static ca.soccer1992.lavaproxy.utils.ComponentUtils.nbt;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class SystemChat extends Packet {
    public boolean isActionBar;
    public Component message;
    public ConnectionTypes getType() { return ConnectionTypes.PLAY; }
    public String name = "SystemChat";
    public void decode (ByteBuf buf, MinecraftVersions proto){
        BinaryTag tag = readTag(buf, proto);
        message = ComponentUtils.fromNBT(tag);
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_19_1)){
            isActionBar = buf.readBoolean();
        } else {
            isActionBar = readVarInt(buf) == 1;

        }
    }

    public void encode (ByteBuf buf, MinecraftVersions proto){
        writeTag(buf, proto, nbt(message, proto));
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_19_1)){
            buf.writeBoolean(isActionBar);
        } else {
            writeVarInt(isActionBar ? 1 : 0, buf);

        }
    }

}
