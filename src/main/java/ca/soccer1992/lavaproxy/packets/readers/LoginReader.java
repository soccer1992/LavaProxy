package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.PluginRequest;
import ca.soccer1992.lavaproxy.packets.client.login.CompressionPacket;
import ca.soccer1992.lavaproxy.packets.client.login.LoginKick;
import ca.soccer1992.lavaproxy.packets.client.login.LoginSuccess;
import ca.soccer1992.lavaproxy.packets.server.PluginResponse;
import ca.soccer1992.lavaproxy.packets.server.*;

import java.util.List;
import java.util.Map;

public class LoginReader extends Reader {
    public Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions = Map.of(
            LoginStart.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_8, 0x00)
            ),
            LoginAck.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x03)
            ),
            PluginResponse.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x02)
            )
    );
    public Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions = Map.of(
            LoginKick.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_8, 0x00)
            ),
            CompressionPacket.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_8, 0x03)
            ),
            LoginSuccess.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_8, 0x02)
            ),
            PluginRequest.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x04)
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