package ca.soccer1992.lavaproxy.packets.clientserver;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;

public class EnterConfiguration extends Packet {
  public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }
  public String name = "EnterConfiguration";
}
