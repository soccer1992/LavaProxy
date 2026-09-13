package ca.soccer1992.lavaproxy.packets.server;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;


import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class EncryptionResponse extends Packet {
    public byte[] sharedSecret;
    public byte[] verifyToken;

    public ConnectionTypes getType() { return ConnectionTypes.LOGIN; }
    public String name = "EncryptionResponse";

    public void setSharedSecret(byte[] secret){this.sharedSecret = secret;}
    public void setVerifyToken(byte[] token){this.verifyToken = token;}

    public void decode (ByteBuf buf, MinecraftVersions proto){

       setSharedSecret(readByteArray(buf));
       setVerifyToken(readByteArray(buf));
        //setUUID(readUUID(buf));
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeByteArray(buf, sharedSecret);
        writeByteArray(buf, verifyToken);

    }

}
