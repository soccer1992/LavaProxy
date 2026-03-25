package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.HandshakeIntent;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.login.CompressionPacket;
import ca.soccer1992.lavaproxy.packets.client.login.LoginKick;
import ca.soccer1992.lavaproxy.packets.client.NBTKick;
import ca.soccer1992.lavaproxy.packets.handlers.*;
import ca.soccer1992.lavaproxy.packets.readers.*;
import ca.soccer1992.lavaproxy.packets.readers.Reader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import net.kyori.adventure.text.Component;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.*;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Connection {
    public final Channel nChannel;
    public MinecraftVersions protocol;
    public InetSocketAddress connectAddr;
    public int compressionAmount;
    public InetSocketAddress addr;
    public Reader protoReader;
    public Handler packetHandler;
    public Player plr;
    public ConnectionTypes conType = ConnectionTypes.HANDSHAKE;
    public ByteBuf heldData = Unpooled.buffer();
    public String connectedServer = null;
    public Connection backendConnection = null;
    public boolean isClosed = false;
    public Component _recentDisconnectMessage;
    public Iterator<String> tryIter = Arrays.stream(Main.trys).iterator();
    public void setCompression(int amt){
        this.compressionAmount = amt;
    }
    public void backendDisconnect(String message){
        backendDisconnect(fromJSON(message));
    }

    public String fillPlaceholders(String placeholder, String kickMsg, String brand){
        String conServer = "";
        if (connectedServer != null){
            conServer = connectedServer;
        }
        return placeholder.replace("{ip}",addr.toString())
                .replace("{player}",plr.toString())
                .replace("{message}",kickMsg)
                .replace("{brand}",brand)
                .replace("{serverName}",conServer)
                .replace("{ipHost}",addr.getHostString());
    }
    public void backendDisconnect(Component message){
        backendConnection = null;
        _recentDisconnectMessage = parser.deserialize(fillPlaceholders(Main.translations.get("backend.player.disconnect"), miniMessage(message), plr.brand)); //Component.text("You have been disconnected from " + connectedServer + ": ").color(NamedTextColor.RED).append(message);

        System.out.println(fillPlaceholders(Main.translations.get("backend.disconnect"), plain(message), plr.brand));

        if (!tryIter.hasNext()){
            noLogDisconnect(_recentDisconnectMessage);
        } else {
            connect(tryIter.next());
        }
        connectedServer = null;

    }
    // to make a lot of random junk very easier
    public Connection(Channel c){
        this.nChannel = c;
        this.addr = (InetSocketAddress) c.remoteAddress();
        setProtocol(47);
        setCompression(-1);
        setReader(new HandshakeReader());
        setHandler(new HandshakeHandler());

        this.plr = new Player(this);

    }
    public void setHandler(Handler r){
        this.packetHandler = r;
    }
    public void setReader(Reader r){
        this.protoReader = r;
    }
    public void connect(String server){
        if (isClosed) return;

        connectedServer = server;
        ArrayList<Object> serverInfo = Main.servers.get(server);
        String host = (String) serverInfo.get(0);
        Integer port = (Integer) serverInfo.get(1);

        Connection con = new ServerConnection().connect(this, HandshakeIntent.LOGIN, host, port);
        if (con == null){
            connectedServer = null;
        } else {
            backendConnection = con;
        }
    }
    public void setProtocol(MinecraftVersions proto){
        this.protocol = proto;
    }

    public void setProtocol(int proto){
        setProtocol(MinecraftVersions.ID_TO_PROTOCOL_CONSTANT.get(proto));
    }
    public void addToHeld(ByteBuf buf){
        heldData.writeBytes(buf);
    }

    public Packet processPacket(ByteBuf p, boolean forceClient) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return protoReader.read(p, protocol.getProtocol(),forceClient);
    }
    public void writePacket(Packet p){
        ByteBuf buf = Unpooled.buffer();
        writeVarInt(protoReader.getPacketFromInfoClient(protocol, p.getClass()), buf);
        _writePacket(p, buf);
    }
    public void writePacketServer(Packet p){
        ByteBuf buf = Unpooled.buffer();
        writeVarInt(protoReader.getPacketFromInfo(protocol, p.getClass()), buf);
        _writePacket(p, buf);

    }
    public void sendCompression(int threshold){
        CompressionPacket pac = new CompressionPacket();
        pac.threshold = threshold;
        writePacket(pac);
        setCompression(threshold);
    }
    public void _writePacket(Packet p, ByteBuf buf){
        if (isClosed) return;

        p.encode(buf, protocol);
        ByteBuf compressedRewritten = Unpooled.buffer();

        if (compressionAmount>=0){

            if (buf.readableBytes()>=compressionAmount){
                writeVarInt(buf.readableBytes(), compressedRewritten);
                byte[] e = new byte[buf.readableBytes()];
                buf.readBytes(e);
                byte[] compressed = compress(e);
                if (compressed == null){
                    close();
                    return;
                }
                compressedRewritten.writeBytes(compressed);



            } else {
                writeVarInt(0, compressedRewritten);
                compressedRewritten.writeBytes(buf, 0, buf.readableBytes());

            }

        } else        compressedRewritten.writeBytes(buf, 0, buf.readableBytes());
        //System.out.println(compressedRewritten.toString(StandardCharsets.UTF_8));

        ByteBuf rewritten14 = Unpooled.buffer();
        writeVarInt(compressedRewritten.readableBytes(), rewritten14);
        rewritten14.writeBytes(compressedRewritten, 0, compressedRewritten.readableBytes());
        buf.release();
        compressedRewritten.release();
        nChannel.writeAndFlush(rewritten14);

    }
    public void disconnect(String reason, boolean isError){
        disconnect(Component.text(reason), isError);
    }
    public void noLogDisconnect(String reason){
        noLogDisconnect(Component.text(reason));
    }
    public void disconnect(){
        disconnect(Component.text("Disconnected"), false);
    }
    public void disconnect(Component reason, boolean isError){
        _disconnect(reason,      isError && (!Main.logErrors));
    }
    public void noLogDisconnect(Component reason){

        _disconnect(reason, true);
    }
    public void _disconnect(Component reason, boolean nolog){
        if (isClosed) return;

        try {
            switch (conType) {
                case ConnectionTypes.HANDSHAKE, ConnectionTypes.PRE_STATUS, ConnectionTypes.STATUS:
                    // just close the connection
                    close();
                    // generic close
                    break;
                case ConnectionTypes.LOGIN,ConnectionTypes.POST_SUCCESS:
                    LoginKick kick = new LoginKick();
                    kick.setReason(json(reason));
                    writePacket(kick);
                    close();
                    break;
                case ConnectionTypes.CONFIG, ConnectionTypes.PLAY:
                    NBTKick nKick = new NBTKick();
                    nKick.setReason(nbt(reason));
                    writePacket(nKick);
                    close();
                    break;
            }
        } catch (Exception e){
            close();
        }
        if (nolog) return;
        System.out.println(fillPlaceholders(Main.translations.get("log.disconnect"), plain(reason), plr.brand));
        //System.out.printf("%s has disconnected for: %s%n",plr,PlainTextComponentSerializer.plainText().serialize(reason));

    }
    public void close(){
        isClosed = true;
        nChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        heldData.release();
    }
    public ByteBuf readPacket(){
        if (isClosed) return null;
        try{
            heldData.markReaderIndex();
            int oldSize = heldData.readableBytes();
        int len = readVarInt(heldData);
        if (len > heldData.readableBytes()){
            return null;
        }
        int lenSize = oldSize-heldData.readableBytes();
        if (oldSize-lenSize>=len){
            // WE CAN READ IT!!
            return heldData.readBytes(len);
        } else {
            heldData.resetReaderIndex();
            return null; // we can't read it

        }} catch (Exception e){return null;}

    }



}
