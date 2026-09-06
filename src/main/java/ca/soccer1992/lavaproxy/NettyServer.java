package ca.soccer1992.lavaproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    private final int port;
    public Channel serverChannel;

    public NettyServer(int port) {
        this.port = port;
    }
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
    }
    public void start() throws Exception {
        EventLoopGroup bossGroup = Main.bossGroup;
        EventLoopGroup workerGroup = Main.nettyGroup;

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.setOption(ChannelOption.TCP_NODELAY, true);

                            Main.CON_AMOUNT += 1;
                            ch.attr(Main.READER).set(new Connection(ch));
                            ch.pipeline().addFirst(new NettyFrameDecoder());
                            ch.pipeline().addLast(new PacketProcessor(false));

                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.printf("[Main] Server started on port %s%n", port);
            System.out.println("Done, players can now connect!");
            serverChannel = future.channel();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}