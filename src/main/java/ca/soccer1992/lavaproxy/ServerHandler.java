package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.InvalidPacket;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.InvalidObjectException;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet in = (Packet) msg;
        //Class<?> c = in.getClass();
        Connection con = ctx.channel().attr(Main.READER).get();
        try {
            if (in instanceof InvalidPacket && con.conType != ConnectionTypes.PLAY){
                con.close();

                throw new InvalidObjectException("InvalidPacket cannot be processed inside of non-PLAY states.");
            }
            if (!con.packetHandler.handle(in, con)) {
                //System.out.println("Packet was not handled...");
                con.close();
            }
        } catch (Exception e){
             con.disconnect(e.getMessage(),true);
        }

        //ctx.write(in); // Echo back
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}