package ca.soccer1992.lavaproxy.packets.server;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.HandshakeIntent;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import static ca.soccer1992.lavaproxy.PacketHelpers.*;

public class HandshakePacket extends Packet {
    public HandshakeIntent intent;
    public int port;
    public String host;
    public MinecraftVersions proto;
    public ConnectionTypes getType() { return ConnectionTypes.HANDSHAKE; }
    public String name = "Handshake";

    public void setPort(int port){this.port = port;}
    public void setProtocol(MinecraftVersions proto){
        this.proto = proto;
    }
    public void setProtocol(int proto){setProtocol(MinecraftVersions.ID_TO_PROTOCOL_CONSTANT.get(proto));}
    public void setIntent(HandshakeIntent intent){this.intent = intent;}
    public void setHost(String host){this.host = host;}
    public void decode (ByteBuf buf){
        setProtocol(readVarInt(buf));
        //System.out.println(this.proto);
        setHost(readString(buf));
        setPort(buf.readUnsignedShort());

        setIntent(HandshakeIntent.getIntentFromID(readVarInt(buf)));
    }
    public void encode(ByteBuf buf){
        writeVarInt(this.proto.getProtocol(), buf);
        writeString(this.host, buf);
        buf.writeShort(this.port);
        writeVarInt(this.intent.getId(), buf);

    }

}
