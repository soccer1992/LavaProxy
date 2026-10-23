package ca.soccer1992.lavaproxy.eventhandlers;

import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.Player;
import ca.soccer1992.lavaproxy.types.Identifier;
import ca.soccer1992.lavaproxy.types.ServerDefinition;
import ca.soccer1992.lavaproxy.utils.ComponentUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static ca.soccer1992.lavaproxy.Main.*;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class PlayerEventHandler extends Handler {
    public void handle(Event event){
        if (event instanceof PluginMessageEvent message){
            Player plr = event.player;
            Player target = plr;
            if (message.channel.equals(Identifier.read("minecraft:brand")) && !message.fromBackend){
                ByteBuf data = Unpooled.copiedBuffer(message.data);

                plr.setBrand(readString(data));
                System.out.println(plr.con.fillPlaceholders("log.brand", "", plr.con.plr.brand));
                data.release();
                //System.out.printf("%s brand: %s%n",c.plr, c.plr.brand);
            }
            if (message.channel.equals(Identifier.read("minecraft:brand")) && message.fromBackend){
                // rewrite
                ByteBuf data = Unpooled.copiedBuffer(message.data);

                try {
                    String rewritten = readString(data);
                    rewritten = plr.con.backendConnection.fillPlaceholders("backend.brand", "", plr.con.backendConnection.plr.brand, "", 0, rewritten);
                    ByteBuf buf = Unpooled.buffer();
                    writeString(rewritten, buf);
                    byte[] out = new byte[buf.readableBytes()];
                    buf.readBytes(out);
                    message.originatingPacket.setData(out);
                    message.data = out;
                    buf.release();
                } finally {
                    data.release();
                }
            }
            if (message.channel.equals(new Identifier("bungeecord", "main")) && message.fromBackend && plr.con.backendConnection.connectedServer.bungeecordChannel){
                ByteBuf data = Unpooled.copiedBuffer(message.data);
                try{
                    target = plr.con.backendConnection.plr;

                    String msgType = readUTF(data);
                    switch (msgType){
                        case "ConnectOther":
                            target = Main.getPlayerFromName(readUTF(data));
                        case "Connect":
                            target.transferToServer(readUTF(data));
                            break;
                        case "IPOther":
                            target = Main.getPlayerFromName(readUTF(data));
                        case "IP": {
                            ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(messageBytes);
                            dos.writeUTF(msgType);
                            if (msgType.equals("IPOther")) dos.writeUTF(target.getName());
                            dos.writeUTF(target.con.addr.getHostString());
                            dos.writeInt(target.con.addr.getPort());
                            plr.sendPluginMessage(message.channel, messageBytes.toByteArray());
                            break;
                        }
                        case "PlayerCount": {
                            String server = readUTF(data);
                            ArrayList<Player> players = server.equals("ALL")
                                    ? new ArrayList<>(Main.players.values())
                                    : getPlayersOnServer(server.toLowerCase());
                            if (players.isEmpty() && !Main.servers.containsKey(server.toLowerCase())) break;
                            ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(messageBytes);
                            dos.writeUTF(msgType);
                            dos.writeUTF(server);
                            dos.writeInt(players.size());
                            plr.sendPluginMessage(message.channel, messageBytes.toByteArray());
                            break;
                        }
                        case "PlayerList": {
                            String server = readUTF(data);
                            ArrayList<Player> players = server.equals("ALL")
                                    ? new ArrayList<>(Main.players.values())
                                    : getPlayersOnServer(server.toLowerCase());
                            if (players.isEmpty() && !Main.servers.containsKey(server.toLowerCase())) break;
                            ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(messageBytes);
                            dos.writeUTF(msgType);
                            dos.writeUTF(server);
                            dos.writeUTF(players.stream()
                                    .map(Player::getName)
                                    .collect(Collectors.joining(", ")));
                            plr.sendPluginMessage(message.channel, messageBytes.toByteArray());
                            break;
                        }
                        case "GetServers": {
                            ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(messageBytes);
                            dos.writeUTF(msgType);
                            dos.writeUTF(String.join(", ", Main.servers.keySet()));
                            plr.sendPluginMessage(message.channel, messageBytes.toByteArray());
                            break;
                        }
                        case "Message": {
                            String targetName = readUTF(data);
                            Component plrMessage = Component.text(readUTF(data));
                            if (targetName.equals("ALL")){
                                for (Player p :  Main.players.values()) p.sendMessage(plrMessage, false);
                            } else {
                                target = Main.getPlayerFromName(targetName);
                                target.sendMessage(plrMessage, false);
                            }
                            break;
                        }
                        case "MessageRaw": {
                            String targetName = readUTF(data);
                            Component plrMessage = ComponentUtils.fromJSON(readUTF(data));
                            if (targetName.equals("ALL")){
                                for (Player p : Main.players.values()) p.sendMessage(plrMessage, false);
                            } else {
                                target = Main.getPlayerFromName(targetName);
                                target.sendMessage(plrMessage, false);
                            }
                            break;
                        }
                        case "GetPlayerServer":
                            target = Main.getPlayerFromName(readUTF(data));
                        case "GetServer": {
                            ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(messageBytes);
                            dos.writeUTF(msgType);

                            if (msgType.equals("GetPlayerServer")) dos.writeUTF(target.getName());
                            dos.writeUTF(target.con.connectedServer.name);
                            plr.sendPluginMessage(message.channel, messageBytes.toByteArray());
                            break;
                        }
                        case "UUIDOther":
                            target = Main.getPlayerFromName(readUTF(data));
                        case "UUID": {
                            ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(messageBytes);
                            dos.writeUTF(msgType);

                            if (msgType.equals("UUIDOther")) dos.writeUTF(target.getName());
                            dos.writeUTF(target.uuid.toString().replace("-", ""));
                            plr.sendPluginMessage(message.channel, messageBytes.toByteArray());
                            break;
                        }
                        case "KickPlayer": {
                            target = Main.getPlayerFromName(readUTF(data));
                            Component plrMessage = Component.text(readUTF(data));
                            target.con.disconnect(plrMessage, false);
                            break;
                        }
                        case "KickPlayerRaw": {
                            target = Main.getPlayerFromName(readUTF(data));
                            Component plrMessage = ComponentUtils.fromJSON(readUTF(data));
                            target.con.disconnect(plrMessage, false);
                            break;
                        }
                        case "ForwardToPlayer": {
                            Player t = getPlayerFromName(readUTF(data));
                            byte[] rest = new byte[data.readableBytes()];
                            data.readBytes(rest);
                            if (t == null || t.con.backendConnection == null) break;
                            t.con.backendConnection.plr.sendPluginMessage(message.channel, rest);
                            break;
                        }
                        case "Forward": {
                            String serverName = readUTF(data);
                            byte[] rest = new byte[data.readableBytes()];
                            data.readBytes(rest);

                            if (serverName.equals("ALL") || serverName.equals("ONLINE")) {
                                // the sender's own server is skipped
                                String current = target.con.connectedServer != null ? target.con.connectedServer.name : null;
                                for (String s : Main.servers.keySet()) {
                                    if (s.equals(current)) continue;
                                    ArrayList<Player> ps = getPlayersOnServer(s);
                                    if (!ps.isEmpty() && ps.getFirst().con.backendConnection != null)
                                        ps.getFirst().con.backendConnection.plr.sendPluginMessage(message.channel, rest);
                                }
                            } else {
                                ArrayList<Player> ps = getPlayersOnServer(serverName.toLowerCase());
                                if (!ps.isEmpty() && ps.getFirst().con.backendConnection != null)
                                    ps.getFirst().con.backendConnection.plr.sendPluginMessage(message.channel, rest);
                            }
                            break;
                        }
                        case "ServerIP":{
                            String serverName = readUTF(data);
                            if (!Main.servers.containsKey(serverName.toLowerCase())) break;
                            ServerDefinition serv = Main.servers.get(serverName.toLowerCase());
                            ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(messageBytes);
                            dos.writeUTF(msgType);
                            dos.writeUTF(serverName);
                            dos.writeUTF(serv.host);
                            dos.writeShort(serv.port);
                            plr.sendPluginMessage(message.channel, messageBytes.toByteArray());
                            break;
                        }
                        default:
                            break;

                    }
                } catch (Exception _) {

                } finally {
                    data.release();

                }
            }

        }
        return;
    }
}