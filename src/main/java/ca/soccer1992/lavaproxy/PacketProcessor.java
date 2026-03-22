package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import static ca.soccer1992.lavaproxy.PacketHelpers.*;
public class PacketProcessor extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Connection con = ctx.channel().attr(Main.READER).get();
        con.disconnect(cause.toString(), true);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){

        // replace the message
        ByteBuf in = (ByteBuf) msg;
        Connection con = ctx.channel().attr(Main.READER).get();
        con.addToHeld(in);
        ByteBuf read = con.readPacket();
        try {
            while (read != null) {
                if (con.compressionAmount>-1){
                    // check first varInt (0 = uncompressed, anything else = compressed length)
                    int compLength = readVarInt(read);
                    if (compLength>0){

                        byte[] decompressed = new byte[compLength];
                        byte[] tmp = new byte[read.readableBytes()];
                        read.readBytes(tmp);
                        decompressed = decompress(tmp);
                        read = Unpooled.buffer();
                        read.writeBytes(decompressed);
                    }

                }
                //System.out.println(read.toString(StandardCharsets.UTF_8));
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
            con.disconnect(e.toString(), true);
        } finally{
            in.release();

        }
    }



}