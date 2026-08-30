package ca.soccer1992.lavaproxy.types;

import ca.soccer1992.lavaproxy.Main;

public class ServerDefinition {
  public String host;
  public int port;
  public String name;
  public String forwardType;
  public String forwardKey;
  public ServerDefinition(String name, String host, int port){
    this.host = host;
    this.port = port;
    this.name = name;
    this.forwardType = Main.forwardType;
    this.forwardKey = Main.forwardKey;
  }
}
