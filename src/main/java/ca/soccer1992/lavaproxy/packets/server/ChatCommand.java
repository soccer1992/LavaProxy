package ca.soccer1992.lavaproxy.packets.server;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class ChatCommand extends Packet {
    public ConnectionTypes getType() { return ConnectionTypes.PLAY; }
    public String name = "ChatCommand";
    public String msg;
    public long ts;
    public long salt;
    public ArrayList<Map<String, byte[]>> sigs = new ArrayList<>();
    public boolean signedPreview;
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(msg,buf);
        buf.writeLong(ts);
        buf.writeLong(salt);

        writeVarInt(sigs.size(), buf);
        for (Map<String, byte[]> sig : sigs) {
            for (Map.Entry<String, byte[]> entry : sig.entrySet()) {
                writeString(entry.getKey(), buf);
                writeVarInt(entry.getValue().length, buf) ;
                buf.writeBytes(entry.getValue());
            }
        }
        buf.writeBoolean(signedPreview);
    }
    public void decode(ByteBuf buf, MinecraftVersions proto){
        msg = readString(buf);
        ts = buf.readLong();
        salt = buf.readLong();
        int length = readVarInt(buf);
        for (int i=0;i<length;i++){
            Map<String, byte[]> data = new HashMap<>();
            byte[] arr = new byte[readVarInt(buf)];
            buf.readBytes(arr);
            data.put(readString(buf), arr);
            sigs.add(data);
        }
        signedPreview = buf.readBoolean();

    }
}
