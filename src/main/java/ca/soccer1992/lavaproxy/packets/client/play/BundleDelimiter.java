package ca.soccer1992.lavaproxy.packets.client.play;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;


public class BundleDelimiter extends Packet {
    public ConnectionTypes getType() { return ConnectionTypes.PLAY; }

    public String name = "BundleDelimiter";


}
