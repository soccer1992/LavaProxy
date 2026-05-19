package ca.soccer1992.lavaproxy.packets.server.play;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class SignedCommand extends Packet {
    public ConnectionTypes getType() { return ConnectionTypes.PLAY; }
    public String name = "SignedCommand";
    public String msg;
    public long ts;
    public long salt;
    public ArrayList<Map<String, byte[]>> sigs = new ArrayList<>();
    public int msgCount;
    public boolean[] acknowledged;
    public byte checksum;
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(msg,buf);
        buf.writeLong(ts);
        buf.writeLong(salt);

        writeVarInt(sigs.size(), buf);
        for (Map<String, byte[]> sig : sigs) {
            for (Map.Entry<String, byte[]> entry : sig.entrySet()) {
                writeString(entry.getKey(), buf);
                buf.writeBytes(entry.getValue());
            }
        }
        writeVarInt(msgCount, buf);
        writeFixedBitSet(buf, acknowledged, 20);
        buf.writeByte(checksum);

    }
    public void decode(ByteBuf buf, MinecraftVersions proto){
        msg = readString(buf);
        ts = buf.readLong();
        salt = buf.readLong();
        int length = readVarInt(buf);
        for (int i=0;i<length;i++){
            Map<String, byte[]> data = new HashMap<>();
            byte[] arr = new byte[256];
            buf.readBytes(arr);
            data.put(readString(buf), arr);
            sigs.add(data);
        }
        msgCount = readVarInt(buf);
        acknowledged = readFixedBitSet(buf, 20);
        checksum = buf.readByte();

    }
}
