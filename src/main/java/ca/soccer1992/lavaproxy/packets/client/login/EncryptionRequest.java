package ca.soccer1992.lavaproxy.packets.client.login;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class EncryptionRequest extends Packet {
    public UUID uuid;

    public String serverID;
    public byte[] publicKey;
    public byte[] verifyToken;
    public boolean authenticate;
    public ConnectionTypes getType() { return ConnectionTypes.LOGIN; }
    public String name = "EncryptionRequest";
    public void setServerID(String serverId){this.serverID = serverId;}
    public void setPublicKey(byte[] key){this.publicKey = key;}
    public void setVerifyToken(byte[] token){this.verifyToken = token;}
    public void setAuthenticate(boolean doAuthentication){this.authenticate = doAuthentication;}

    public void decode (ByteBuf buf, MinecraftVersions proto){
        setServerID(readString(buf));
        setPublicKey(readByteArray(buf));
        setVerifyToken(readByteArray(buf));
        setAuthenticate(!proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_2) || buf.readBoolean());

    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(serverID, buf);
        writeByteArray(buf, publicKey);
        writeByteArray(buf, verifyToken);
        if (proto.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_2)) buf.writeBoolean(authenticate);
    }

}
