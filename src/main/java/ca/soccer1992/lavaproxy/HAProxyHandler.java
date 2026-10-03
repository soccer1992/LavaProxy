package ca.soccer1992.lavaproxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ProtocolDetectionResult;
import io.netty.handler.codec.ProtocolDetectionState;
import io.netty.handler.codec.haproxy.*;

import java.net.InetSocketAddress;
import java.util.List;

public class HAProxyHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        ProtocolDetectionResult<HAProxyProtocolVersion> result = HAProxyMessageDecoder.detectProtocol(in);

        if (result.state() == ProtocolDetectionState.NEEDS_MORE_DATA) {
            return; // wait for more bytes
        }

        if (result.state() == ProtocolDetectionState.INVALID) {
            if (!Main.haProxyOptional) {
                ctx.close(); // HAProxy required
                return;
            }
            ctx.pipeline().remove(this); // plain connection, just yeet it out of existance
            return;
        }

        // haproxy
        ChannelPipeline p = ctx.pipeline();
        p.addAfter(ctx.name(), "haproxy-decoder", new HAProxyMessageDecoder());
        p.addAfter("haproxy-decoder", "haproxy-handler", new SimpleChannelInboundHandler<HAProxyMessage>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HAProxyMessage msg) {
                if (msg.command() == HAProxyCommand.PROXY && msg.sourceAddress() != null) {
                    ctx.channel().attr(Main.READER).get().addr =
                            new InetSocketAddress(msg.sourceAddress(), msg.sourcePort());
                }
                ctx.pipeline().remove(this);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                cause.printStackTrace();
                ctx.close();
            }
        });

        ctx.pipeline().remove(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}