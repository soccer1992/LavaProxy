package ca.soccer1992.lavaproxy.packets.server;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static ca.soccer1992.lavaproxy.PacketHelpers.*;

public class LoginStart extends Packet {
    public UUID uuid;
    public String playerName;

    public ConnectionTypes getType() { return ConnectionTypes.LOGIN; }
    public String name = "LoginStart";

    public void setUUID(UUID uuid){this.uuid = uuid;}
    public void setName(String name){this.playerName = name;}

    public void decode (ByteBuf buf, MinecraftVersions proto){

        setName(readString(buf));
        setUUID(UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8)));

        // debug do not leave in prod bro do not do this
        if (false){ // set to false to disable future me
            if (proto.getProtocol()<=MinecraftVersions.MINECRAFT_1_19.getProtocol()){
                return;
            }
            if (proto.getProtocol()<MinecraftVersions.MINECRAFT_1_19_3.getProtocol()){
                buf.readBoolean(); // no sig
            }
            if (proto.getProtocol()<MinecraftVersions.MINECRAFT_1_20_2.getProtocol()){
                buf.readBoolean(); // has UUID
            }

            setUUID(readUUID(buf));



        }
        //setUUID(readUUID(buf));
    }
    public void encode(ByteBuf buf, MinecraftVersions proto){
        writeString(playerName,buf);
        // 1.20.2 is norm packet Y
        // 1.19.3 is 1.20.2 with has player uuid Y
        // 1.19.2 same as 1.19.3 with no sig data Y
        // 1.19 is <1.19 with no sig data Y


        if (proto.getProtocol()<=MinecraftVersions.MINECRAFT_1_19.getProtocol()){
            return;
        }
        if (proto.getProtocol()<MinecraftVersions.MINECRAFT_1_19_3.getProtocol()){
            buf.writeBoolean(false); // no sig
        }

        if (proto.getProtocol()<MinecraftVersions.MINECRAFT_1_20_2.getProtocol()){
            buf.writeBoolean(true); // has UUID
        }

        writeUUID(uuid,buf);

    }

}
