package ca.soccer1992.lavaproxy.packets.clientserver;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;


public class EnterConfiguration extends Packet {
  public ConnectionTypes getType() { return ConnectionTypes.CONFIG; }

  public String name = "EnterConfiguration";
  public void decode (ByteBuf buf, MinecraftVersions proto){
    System.out.println("EnterConfiguration bytes: " + ByteBufUtil.hexDump(buf.duplicate()));
  }
}
