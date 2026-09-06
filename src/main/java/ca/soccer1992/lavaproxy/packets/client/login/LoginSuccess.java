package ca.soccer1992.lavaproxy.packets.client.login;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.types.GameProfile;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class LoginSuccess extends Packet {
    public Connection con;
    public UUID uuid;
    public UUID sessionUUID;

    public String plrName;

    public ConnectionTypes getType() { return ConnectionTypes.LOGIN; }
    public String name = "LoginSuccess";
    public void setUUID(UUID uuid){this.uuid = uuid;}
    public void setName(String plrName){this.plrName = plrName;}
    public void setSessionUUID(UUID uuid){this.sessionUUID = uuid;}

    public void setConnection(Connection con){this.con = con;}

    public void decode (ByteBuf buf, MinecraftVersions proto){
        // 1.8
        if (proto.getProtocol()<MinecraftVersions.MINECRAFT_26_1.getProtocol()) {

            if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_16.getProtocol()) {
                setUUID(UUID.fromString(readString(buf)));
            } else {
                setUUID(readUUID(buf));
            }
            setName(readString(buf));
        } else {
            GameProfile profile = GameProfile.read(buf, proto);
            setUUID(profile.uuid);
            setName(profile.name);
            setSessionUUID(readUUID(buf));
        }
        // 1.16


    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        if (proto.getProtocol()<MinecraftVersions.MINECRAFT_26_1.getProtocol()) {
            if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_16.getProtocol()) {
                writeString(uuid.toString(), buf);
            } else {
                writeUUID(uuid, buf);
            }
            writeString(plrName, buf);
            if (proto.getProtocol() > MinecraftVersions.MINECRAFT_1_19.getProtocol()) {
                writeVarInt(0, buf);
            }
            if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_20_5.getProtocol()) return;
            // 1.20.6 strict error handle to 1.21.4

            if (proto.getProtocol() < MinecraftVersions.MINECRAFT_1_21_4.getProtocol()) {
                buf.writeBoolean(false);
            }
        } else {
            // WHY IS IT A GAMEPROFILE
            new GameProfile(uuid, plrName, null).write(buf, proto);
            writeUUID(sessionUUID, buf);
        }
    }

}
