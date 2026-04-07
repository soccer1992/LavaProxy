package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet in = (Packet) msg;
        //Class<?> c = in.getClass();
        Connection con = ctx.channel().attr(Main.READER).get();
        try {
            if (!con.packetHandler.handle(in, con)) {
                //System.out.println("Packet was not handled...");
                con.close();
            }
        } catch (Exception e){
            e.printStackTrace();
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