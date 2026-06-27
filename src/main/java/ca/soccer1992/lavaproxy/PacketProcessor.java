package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;
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
    public void channelInactive(ChannelHandlerContext ctx){
        Connection con = ctx.channel().attr(Main.READER).get();


        if (con.backendConnection != null){

            if (!this.client){
                con.backendConnection.close();
            }
        }
        ctx.fireChannelInactive();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){

        // replace the message
        ByteBuf read = (ByteBuf) msg;
        ByteBuf release = read;
        Connection con = ctx.channel().attr(Main.READER).get();
        try {
            if (con.compressionAmount>-1){
                // check first varInt (0 = uncompressed, anything else = decompressed length)
                int compLength = readVarInt(read);
                if (compLength>0){
                    byte[] tmp = new byte[read.readableBytes()];
                    read.readBytes(tmp);
                    byte[] decompressed = decompress(tmp, compLength);
                    if (decompressed == null){
                        con.close();
                        return;
                    }
                    read.release();
                    read = ctx.alloc().buffer();
                    read.writeBytes(decompressed);
                    release = read;
                }
            }
            Packet p = con.processPacket(read,client);
            if (p == null) {
                // invalid packet
                ctx.close();
                return;
            }
            ctx.fireChannelRead(p);

        } catch (Exception e){
            e.printStackTrace();
            con.disconnect(Component.text(e.toString()), true);
        } finally{
            if (release.refCnt() > 0) release.release();

        }
    }



}