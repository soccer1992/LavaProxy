package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.LoginKick;
import ca.soccer1992.lavaproxy.packets.handlers.*;
import ca.soccer1992.lavaproxy.packets.readers.*;
import ca.soccer1992.lavaproxy.packets.readers.Reader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import static ca.soccer1992.lavaproxy.PacketHelpers.*;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Connection {
    public final Channel nChannel;
    public MinecraftVersions protocol;
    public InetSocketAddress connectAddr;

    public InetSocketAddress addr;
    public Reader protoReader;
    public Handler packetHandler;
    public Player plr;
    public ConnectionTypes conType = ConnectionTypes.HANDSHAKE;
    public ByteBuf heldData = Unpooled.buffer();
    // to make a lot of random junk very easier
    public Connection(Channel c){
        this.nChannel = c;
        this.addr = (InetSocketAddress) c.remoteAddress();
        setProtocol(47);
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
    public void setProtocol(MinecraftVersions proto){
        this.protocol = proto;
    }

    public void setProtocol(int proto){
        setProtocol(MinecraftVersions.ID_TO_PROTOCOL_CONSTANT.get(proto));
    }
    public void addToHeld(ByteBuf buf){
        heldData.writeBytes(buf);
    }

    public Packet processPacket(ByteBuf p) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return protoReader.read(p, protocol.getProtocol());
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
    public void _writePacket(Packet p, ByteBuf buf){
        p.encode(buf);
        ByteBuf rewritten14 = Unpooled.buffer();
        writeVarInt(buf.readableBytes(), rewritten14);
        rewritten14.writeBytes(buf, 0, buf.readableBytes());
        buf.release();
        nChannel.writeAndFlush(rewritten14);

    }

    public void disconnect(){
        disconnect("Disconnected", false);
        return ;
    }
    public void disconnect(String reason, boolean isError){
        _disconnect(reason,      isError && (!Main.logErrors));
    }
    public void noLogDisconnect(String reason){

        _disconnect(reason, true);
    }
    public void _disconnect(String reason, boolean nolog){

        switch (conType){
            case ConnectionTypes.HANDSHAKE:
                // just close the connection
                close();
                // generic close
                break;
            case ConnectionTypes.LOGIN:
                LoginKick kick = new LoginKick();
                kick.setReason(reason);
                writePacket(kick);
                close();
        }
        if (nolog) return;
        System.out.printf("%s has disconnected for: %s%n",plr,reason);

    }
    public void close(){
        nChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        heldData.release();
    }
    public ByteBuf readPacket(){
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
