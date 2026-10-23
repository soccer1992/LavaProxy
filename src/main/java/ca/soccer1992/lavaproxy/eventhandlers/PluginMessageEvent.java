package ca.soccer1992.lavaproxy.eventhandlers;

import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.types.Identifier;

public class PluginMessageEvent extends Event {
    public Identifier channel;
    public byte[] data;
    public PluginMessage originatingPacket;
}
