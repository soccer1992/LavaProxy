package ca.soccer1992.lavaproxy.packets.client;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

import static ca.soccer1992.lavaproxy.utils.NBTUtil.*;


public class NBTKick extends Packet {
    public CompoundTag reason;

    public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
    public String name = "NBTKick";
    public String reasonJSON(){
        return snbt(reason);
    }
    public void setReason(CompoundTag reason){this.reason = reason;}

    public void decode (ByteBuf buf, MinecraftVersions proto){
        try {
            setReason((CompoundTag) streamAuto(buf, proto).getTag());
        } catch (Exception ignored){
        }
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buf)) {
            convertAuto(reason, stream, proto);
            //NBTWriter.write(reason, stream, false, proto.getProtocol()>=MinecraftVersions.MINECRAFT_1_20_2.getProtocol());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
