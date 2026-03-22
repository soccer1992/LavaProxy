package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.server.LoginStart;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;


public class LoginHandler extends Handler{
    public boolean handle(Packet p, Connection c){

        if (p instanceof LoginStart packet){
            if (!packet.playerName.matches("[a-zA-Z0-9\\p{Punct}]+")){
                c.noLogDisconnect("\"Invalid player name\"");
                return true;
            }
            c.plr.setName(packet.playerName);
            c.plr.setUUID(packet.uuid);
            System.out.printf("Player %s (%s) has started login%n",c.plr, c.addr.getHostString());
            c.disconnect(ComponentUtils.parser.deserialize("<rainbow>Testing</rainbow>"),false);
            return true;

        }


        return false;
    }
}
