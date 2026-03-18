package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.LoginKick;
import ca.soccer1992.lavaproxy.packets.server.*;

import java.util.List;
import java.util.Map;

public class LoginReader extends Reader {
    public Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions = Map.of(
            LoginStart.class, List.of(
                    new DefinitionPair(MinecraftVersions.MINECRAFT_1_8, 0x00)
            )
    );
    public Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions = Map.of(
            LoginKick.class, List.of(
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