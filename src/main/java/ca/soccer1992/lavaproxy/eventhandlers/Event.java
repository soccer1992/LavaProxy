package ca.soccer1992.lavaproxy.eventhandlers;

import ca.soccer1992.lavaproxy.Player;
import ca.soccer1992.lavaproxy.packets.Packet;

public abstract class Event {
    public Packet originatingPacket = null;
    public boolean cancel = false;
    public Player player = null;
    public boolean fromBackend;

}
