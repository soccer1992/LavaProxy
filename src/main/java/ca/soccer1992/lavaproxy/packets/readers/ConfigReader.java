package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.*;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.server.*;


import java.util.List;
import java.util.Map;

public class ConfigReader extends Reader {
    public Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions = Map.of(

            ClientInfo.class,List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x00)
            ),
            PluginMessage.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x01),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x02)
            )
    );
    public Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions = Map.of(
            NBTKick.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x01),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x02)
            ),
            PluginMessage.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x00),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x01)
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