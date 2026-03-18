package ca.soccer1992.lavaproxy.packets;

import ca.soccer1992.lavaproxy.MinecraftVersions;

public record DefinitionPair(MinecraftVersions protocol, int packetID) {}