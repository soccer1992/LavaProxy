package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.eventhandlers.PluginMessageEvent;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.InvalidPacket;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.clientserver.EnterConfiguration;
import ca.soccer1992.lavaproxy.packets.clientserver.KeepAlive;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.readers.ConfigReader;
import ca.soccer1992.lavaproxy.packets.readers.PlayReader;
import ca.soccer1992.lavaproxy.packets.server.*;
import ca.soccer1992.lavaproxy.packets.server.play.ChatCommand;
import ca.soccer1992.lavaproxy.packets.server.play.SignedCommand;
import ca.soccer1992.lavaproxy.packets.server.play.UnsignedChat;
import ca.soccer1992.lavaproxy.packets.server.play.UnsignedCommand;
import ca.soccer1992.lavaproxy.types.Identifier;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.Arrays;

public class PlayHandler extends Handler{

    public static void handlePlay(Connection c, boolean modifyBackendConnection){
        c.conType = ConnectionTypes.PLAY;
        c.setReader(new PlayReader());
        c.setHandler(new PlayHandler());

        if (modifyBackendConnection) c.backendConnection.setReader(new PlayReader());
        if (modifyBackendConnection) c.backendConnection.setHandler(new ca.soccer1992.lavaproxy.packets.handlers.client.PlayHandler());
        c.tryIter = Arrays.stream(Main.trys).iterator();
        if (Main.trys[0].equals(c.connectedServer.name)) c.tryIter.next();
        //c.disconnect(ComponentUtils.parser.deserialize("<rainbow>Connected to " + c.connectedServer + "</rainbow>"), false);
    }
    public boolean handle(Packet p, Connection c){
        //System.out.printf("[IN] " + p.getClass().getSimpleName() + " ");

        if (p instanceof EnterConfiguration){
            c.setReader(new ConfigReader());
            c.conType = ConnectionTypes.CONFIG;
            c.setHandler(new ConfigHandler());
            if (c.waitingServer != null) {
                String server = c.waitingServer.name;
                c.waitingServer = null;

                Connection oldBackend = c.backendConnection;
                c.backendConnection = null;
                if (oldBackend != null && !oldBackend.isClosed) {
                    oldBackend.close();
                    oldBackend.nChannel.closeFuture().addListener((ChannelFutureListener) future -> c.connect(server));
                } else {
                    c.connect(server);
                }

            } else {
                c.backendConnection.writePacketServer(p);
            }
            return true;
        }
        if (c.backendConnection == null) return true;
        if (c.waitingServer!=null) return true;

        if (p instanceof UnsignedChat packet){
            if (packet.msg.startsWith("/")){
                if (!c.plr.executeCommand(packet.msg.substring(1))) c.backendConnection.writePacketServer(packet);
            } else {
                c.backendConnection.writePacketServer(packet);
            }
            return true;
        }
        if (p instanceof UnsignedCommand packet){
            if (!c.plr.executeCommand(packet.msg)) c.backendConnection.writePacketServer(packet);

            return true;
        }
        if (p instanceof SignedCommand packet){
            if (!c.plr.executeCommand(packet.msg)) c.backendConnection.writePacketServer(packet);

            return true;
        }
        if (p instanceof ChatCommand packet){
            if (!c.plr.executeCommand(packet.msg)) c.backendConnection.writePacketServer(packet);

            return true;
        }
        if (p instanceof ClientInfo packet) {
            c.plr.setInfo(packet);
            c.backendConnection.writePacketServer(packet);

            return true;
        }
        if (p instanceof PluginMessage packet) {
            PluginMessageEvent event = new PluginMessageEvent();
            event.channel = Identifier.read(packet.channel);
            event.fromBackend = false;
            event.data = packet.data;
            event.originatingPacket = packet;
            event.cancel = false;
            event.player = c.plr;
            for (ca.soccer1992.lavaproxy.eventhandlers.Handler handler : Main.eventHandlers){
                handler.handle(event);
                if (event.cancel) return true;
            }
            c.backendConnection.writePacketServer(event.originatingPacket);
            return true;
        }
        if (p instanceof InvalidPacket packet ){
            c.backendConnection.writePacketServer(packet);
            return true;
        }
        if (p instanceof KeepAlive packet) {
            if (c.keepAliveList.remove(packet.id) != null) return true;
            c.backendConnection.writePacketServer(packet);
            return true;
        }
        return false;
    }
}
