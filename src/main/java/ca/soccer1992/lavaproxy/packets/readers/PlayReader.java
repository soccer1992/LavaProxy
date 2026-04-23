package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.*;
import ca.soccer1992.lavaproxy.packets.clientserver.*;
import ca.soccer1992.lavaproxy.packets.server.*;

import java.util.List;
import java.util.Map;

public class PlayReader extends Reader {
    public Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions = Map.of(

            PluginMessage.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x17),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x9),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12, 0xA),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12_1, 0x9),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0xA),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0xB),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_17, 0xA),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0xC),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0xD),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0xC),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0xD),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0xF),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_3, 0x10),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x12),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x14),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_6, 0x15)


            )
    );
    public Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions = Map.of(
            NBTKick.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_8, 0x40),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x1a),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x1b),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x1a),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_15, 0x1b),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16, 0x1a),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16_2, 0x19),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_17, 0x1a),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x17),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x19),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x17),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x1a),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x1b),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x1d),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_5, 0x1c),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_9, 0x20)

            ),
            ClientInfo.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x15),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x4),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12, 0x5),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12_1, 0x7),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x8),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x7),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x8),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x9),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0xA),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0xC),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0xD)

            ),
            PluginMessage.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x3f),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x18),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x19),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x18),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_15, 0x19),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16, 0x18),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16_2, 0x17),

                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_17, 0x18),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x15),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x16),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x15),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x17),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x18),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x19),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_5, 0x18)

                    ),
            Login.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x01),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x23),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x25),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x25),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_15, 0x26),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16, 0x25),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16_2, 0x24),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_17, 0x26),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x23),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x25),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x24),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x28),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x29),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x2B),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x2C),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_5, 0x2B),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_9, 0x30)
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