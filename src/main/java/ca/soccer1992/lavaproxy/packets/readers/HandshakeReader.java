package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.server.HandshakePacket;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Map;

import static ca.soccer1992.lavaproxy.PacketHelpers.*;

public class HandshakeReader extends Reader {
    public Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions = Map.of(
            HandshakePacket.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_8, 0x00)
            )
    );
    @Override
    protected Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions(){
        return serverDefinitions;

    }
    @Override
    protected Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions(){
        return clientDefinitions;

    }

}