package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.*;
import ca.soccer1992.lavaproxy.packets.client.play.*;
import ca.soccer1992.lavaproxy.packets.clientserver.*;
import ca.soccer1992.lavaproxy.packets.server.*;
import ca.soccer1992.lavaproxy.packets.server.play.ChatCommand;
import ca.soccer1992.lavaproxy.packets.server.play.SignedCommand;
import ca.soccer1992.lavaproxy.packets.server.play.UnsignedChat;
import ca.soccer1992.lavaproxy.packets.server.play.UnsignedCommand;

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


            ),
            KeepAlive.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x00),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x0B),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12, 0x0C),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12_1, 0x0B),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x0E),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x0F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16, 0x10),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_17, 0x0F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x11),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x12),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x11),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x12),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x14),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_3, 0x15),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x18),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x1A),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_6, 0x1B)
            ),
            UnsignedChat.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x01),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x02),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12, 0x03),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12_1, 0x02),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x03),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, -1)
            ),
            ChatCommand.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x03),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x04),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3   , -1)
            ),
            SignedCommand.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x04),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x05),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x06),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_6, 0x07)
            ),
            UnsignedCommand.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x04),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x05),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_6, 0x06)
            ),
            EnterConfiguration.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x0B),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x0C),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x0E),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_6, 0x0F)
            )
    );
    public Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions = Map.of(
            ca.soccer1992.lavaproxy.packets.client.UnsignedChat.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_8, 0x02),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x0F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x0E),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_15, 0x0F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16, 0x0E),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_17, 0x0F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, -1)
            ),
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

            SystemChat.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x5F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x62),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x60),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x64),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x67),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_3, 0x69),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x6C),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x73),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_5, 0x72),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_9, 0x77)
            ),
            EnterConfiguration.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x65),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_3, 0x67),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x69),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x70),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_5, 0x6F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_9, 0x74)

                    ),
            ClientInfo.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x15),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x04),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12, 0x05),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_12_1, 0x04),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x05),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x07),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x08),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x07),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x08),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x09),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x0A),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x0C),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_6, 0x0D)

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
            ),
            BundleDelimiter.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x00)
            ),
            KeepAlive.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x00),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x1F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x21),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x20),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_15, 0x21),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16, 0x20),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_16_2, 0x1F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_17, 0x21),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x1E),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x20),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x1F),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x23),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x24),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x26),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x27),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_5, 0x26),
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_9, 0x2B)
            )
            //Respawn.class, List.of(
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_7_2, 0x07),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_9, 0x33),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_12, 0x34),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_12_1, 0x35),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_13, 0x38),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_14, 0x3A),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_15, 0x3B),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_16, 0x3A),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_16_2, 0x39),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_17, 0x3D),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_19, 0x3B),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_1, 0x3E),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_3, 0x3D),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_19_4, 0x41),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_2, 0x43),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_3, 0x45),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_20_5, 0x47),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_2, 0x4C),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_5, 0x4B),
            //        new DefinitionPair(MinecraftVersions.MINECRAFT_1_21_9, 0x50)
            //)
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