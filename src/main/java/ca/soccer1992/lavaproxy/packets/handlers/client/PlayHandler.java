package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.eventhandlers.PluginMessageEvent;
import ca.soccer1992.lavaproxy.packets.InvalidPacket;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.NBTKick;
import ca.soccer1992.lavaproxy.packets.client.UnsignedChat;
import ca.soccer1992.lavaproxy.packets.client.play.*;
import ca.soccer1992.lavaproxy.packets.clientserver.EnterConfiguration;
import ca.soccer1992.lavaproxy.packets.clientserver.KeepAlive;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
import ca.soccer1992.lavaproxy.types.Identifier;
import net.kyori.adventure.text.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.fromJSON;

public class PlayHandler extends Handler {

    public boolean handle(Packet p, Connection c) {
        if (c.backendConnection.waitingServer!=null) return true;
        //System.out.println("[OUT] " + p.getClass().getSimpleName());
        if (p instanceof final NBTKick packet){
            try {
                c.backendConnection.backendDisconnect(fromJSON(packet.reasonJSON().toString()));
            } catch (Exception e){
                c.backendConnection.disconnect(e.toString(), true);
            }
            return true;
        }
        if (p instanceof Login packet){
            c.backendConnection.lastServer = null;
            if (c.protocol.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_2)){
               packet.dimension = c._dimensionMap.get(packet.dimensionID);
            }
            c._dimensionName = packet.dimension;
            c.backendConnection.writePacket(packet);
            System.out.println(c.backendConnection.fillPlaceholders("log.connected", "", c.backendConnection.plr.brand));
            if (!c.backendConnection._recentDisconnectMessage.equals(Component.empty())) {
                c.backendConnection.plr.sendMessage(c.backendConnection._recentDisconnectMessage, false);
            }
            c.backendConnection._recentDisconnectMessage = Component.empty();
            for (String permission : c.backendConnection.connectedServer.defaultPermissions){
                c.backendConnection.plr.permissions.put(permission, true);
            }
            c.backendConnection.tryIter = Arrays.stream(Main.trys).iterator();
            if (Main.trys[0].equals(c.backendConnection.connectedServer.name)) c.backendConnection.tryIter.next();
            //c.backendConnection.plr.sendMessage(Component.text("if you see this, it worked."),false);

            //c._dimInfo = c._dimensionCodec.getCompoundTag(packet.dimension);
            //System.out.println(c._dimInfo);
            //c.backendConnection.backendDisconnect(ComponentUtils.parser.deserialize("<rainbow>Simulation distance: " + packet.simDist + "</rainbow><br>" + c._dimensionName));
            if (c.backendConnection.connectedServer.bungeecordChannel){
                byte[] registerBytes = "bungeecord:main".getBytes(StandardCharsets.US_ASCII);
                c.plr.sendPluginMessage(Identifier.read("minecraft:register"), registerBytes);
            }
            return true;
        }
        if (p instanceof EnterConfiguration){
            c.backendConnection.writePacket(p);

            return true;
        }
        if (p instanceof UnsignedChat packet){
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof PluginMessage packet) {
            PluginMessageEvent event = new PluginMessageEvent();
            event.channel = Identifier.read(packet.channel);
            event.fromBackend = true;
            event.data = packet.data;
            event.originatingPacket = packet;
            event.cancel = false;
            event.player = c.plr;
            System.out.println("backend playhandler: " + event.channel);

            for (ca.soccer1992.lavaproxy.eventhandlers.Handler handler : Main.eventHandlers){
                handler.handle(event);
                if (event.cancel) return true;
            }
            c.backendConnection.writePacket(event.originatingPacket);
            return true;
        }
        if (p instanceof SystemChat packet){
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof BundleDelimiter packet) {
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof InvalidPacket packet ){
            c.backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof KeepAlive packet) {
            if (c.keepAliveList.remove(packet.id) != null) return true;
            c.backendConnection.writePacket(packet);
            return true;
        }
        return false;
    }
}
