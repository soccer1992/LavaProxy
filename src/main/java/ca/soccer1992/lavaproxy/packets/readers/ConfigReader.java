package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.*;
import ca.soccer1992.lavaproxy.packets.clientserver.*;
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
            ),
            KeepAlive.class, List.of(
                    new DefinitionPair( MinecraftVersions.MINECRAFT_1_20_2, 0x03 ),
                    new DefinitionPair( MinecraftVersions.MINECRAFT_1_20_5, 0x04 )
            ),
            FinishConfiguration.class, List.of(
                    new DefinitionPair( MinecraftVersions.MINECRAFT_1_20_2, 0x02 ),
                    new DefinitionPair( MinecraftVersions.MINECRAFT_1_20_5, 0x03 )
            ),
            KnownPacks.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x07)
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
            ),
            KeepAlive.class, List.of(
                    new DefinitionPair( MinecraftVersions.MINECRAFT_1_20_2, 0x03 ),
                    new DefinitionPair( MinecraftVersions.MINECRAFT_1_20_5, 0x04 )
            ),
            Transfer.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x0B)
            ),
            FinishConfiguration.class, List.of(
                    new DefinitionPair( MinecraftVersions.MINECRAFT_1_20_2, 0x02 ),
                    new DefinitionPair( MinecraftVersions.MINECRAFT_1_20_5, 0x03 )
            ),
            FeatureFlags.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x07),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_3, 0x08),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x0C)

            ),
            KnownPacks.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x0E)
            ),
            UpdateTags.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x08),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_3, 0x09),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x0D)


            ),
            RegistryData.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x05),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x07)
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