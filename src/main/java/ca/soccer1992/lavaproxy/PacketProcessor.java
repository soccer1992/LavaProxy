package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.kyori.adventure.text.Component;


import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;
public class PacketProcessor extends ChannelDuplexHandler {
    public boolean client;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Connection con = ctx.channel().attr(Main.READER).get();
        con.disconnect(Component.text(cause.toString()), true);
    }
    public PacketProcessor(boolean client){
        this.client = client;
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
                    // check first varInt (0 = uncompressed, anything else = decompressed length)
                    int compLength = readVarInt(read);
                    if (compLength>0){

                        byte[] tmp = new byte[read.readableBytes()];
                        read.readBytes(tmp);
                        byte[] decompressed = decompress(tmp, compLength);
                        read = Unpooled.buffer();
                        read.writeBytes(decompressed);
                    }

                }
                //System.out.println(read.toString(StandardCharsets.UTF_8));
                Packet p = con.processPacket(read,client);
                read.release();
                if (p == null) {
                    // invalid packet
                    ctx.close();
                    return;
                }
                ctx.fireChannelRead(p);
                read = con.readPacket();

            }
        } catch (Exception e){
            con.disconnect(Component.text(e.toString()), true);
        } finally{
            in.release();

        }
    }



}