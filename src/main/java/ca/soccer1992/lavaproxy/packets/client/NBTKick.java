package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.*;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.nbt.BinaryTag;

import static ca.soccer1992.lavaproxy.utils.NBTUtil.*;


public class NBTKick extends Packet {
    public BinaryTag reason;

    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "NBTKick";
    public JsonElement reasonJSON(){
        return deserialize(reason, true);
    }
    public void setReason(BinaryTag reason){this.reason = reason;}

    public void decode (ByteBuf buf, MinecraftVersions proto){
        setReason(readTag(buf, proto));
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeTag(buf, proto, reason);
    }

}
