package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class PacketProcessor extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Connection con = ctx.channel().attr(Main.READER).get();
        con.disconnect(cause.toString(), true);
        return;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // replace the message
        ByteBuf in = (ByteBuf) msg;
        Connection con = ctx.channel().attr(Main.READER).get();
        con.addToHeld(in);
        ByteBuf read = con.readPacket();

        try {
            while (read != null) {
                System.out.println(read.toString(StandardCharsets.UTF_8));
                Packet p = con.processPacket(read);
                if (p == null) {
                    // invalid packet
                    ctx.close();
                    return;
                }
                ctx.fireChannelRead(p);
                read.release();
                read = con.readPacket();

            }
        } catch (Exception e){
            e.printStackTrace();
            con.disconnect(e.toString(), true);
        } finally{
            in.release();

        }
    }



}