package ca.soccer1992.lavaproxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.*;

public class NettyFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        Connection conRef = ctx.channel().attr(Main.READER).get();
        byteBuf.markReaderIndex();

        if (byteBuf.readableBytes() < 1) return;

        int len;
        try {
            len = readVarInt(byteBuf);
        } catch (IndexOutOfBoundsException e) {
            byteBuf.resetReaderIndex();
            return;
        }
        if (len > MAX_PACKET_SIZE){
            conRef.close();
            return;
        }

        if (byteBuf.readableBytes() < len) {
            byteBuf.resetReaderIndex();
            return;
        }
        list.add(byteBuf.readBytes(len));
    }
}
