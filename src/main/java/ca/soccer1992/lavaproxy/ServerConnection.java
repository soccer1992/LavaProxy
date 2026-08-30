package ca.soccer1992.lavaproxy;

import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.HandshakeIntent;
import ca.soccer1992.lavaproxy.packets.handlers.client.LoginHandler;
import ca.soccer1992.lavaproxy.packets.readers.LoginReader;
import ca.soccer1992.lavaproxy.packets.server.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ServerConnection {
    public Connection connect(Connection con, HandshakeIntent intent, String host, int port, String serverName) {

        EventLoopGroup group = Main.nettyGroup;
        final Connection[] throughConnection = {null};
        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.setOption(ChannelOption.TCP_NODELAY, true);
                            Connection c = new Connection(ch);
                            ch.attr(Main.READER).set(c);
                            throughConnection[0] = c;
                            ch.attr(Main.BACKEND).set(con);
                            c.backendConnection = con;
                            con.backendConnection = c;
                            con.connectedServer = serverName;
                            c.connectedServer = serverName;
                            ch.pipeline().addFirst(new NettyFrameDecoder());

                            ch.pipeline().addLast(new PacketProcessor(true));

                            ch.pipeline().addLast(new ServerHandler());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelInactive(ChannelHandlerContext ctx){
                                    //if (c.heldData.refCnt() > 0) c.heldData.release();

                                    if (con.backendConnection == c && c.connectedServer != null && c.connectedServer.equals(serverName)) { // only disconnect if still the active backend
                                        c.backendConnection.backendDisconnect("Connection closed");
                                    }
                                    c.close();
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    //
                                    c.setProtocol(con.protocol);
                                    HandshakePacket p = new HandshakePacket();
                                    p.setIntent(intent);
                                    p.setProtocol(con.protocol);
                                    p.setHost(con.connectAddr.getHostString());
                                    p.setPort(con.connectAddr.getPort());
                                    c.writePacketServer(p);
                                    c.isBackend = true;
                                    if (intent.getId()>1) {
                                        LoginStart login = new LoginStart();
                                        login.setName(con.plr.name);
                                        login.setUUID(con.plr.uuid);
                                        c.plr.uuid = con.plr.uuid;
                                        c.plr.name = con.plr.name;
                                        c.plr.brand = con.plr.brand;
                                        c.plr.infoPacket = con.plr.infoPacket;
                                        //c.backendConnection = con;
                                        c.setReader(new LoginReader());
                                        c.conType = ConnectionTypes.LOGIN;
                                        c.setHandler(new LoginHandler());
                                        c.writePacketServer(login);
                                        if (con.conType != ConnectionTypes.HANDSHAKE
                                                && con.conType != ConnectionTypes.LOGIN
                                                && con.conType != ConnectionTypes.PRE_STATUS
                                                && con.conType != ConnectionTypes.STATUS){
                                            // send a keepalive
                                            //con.sendKeepAlive();
                                        }

                                    }

                                }


                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    ctx.close();
                                }
                            });
                        }
                    });

            bootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    con.backendDisconnect(future.cause().toString());
                }
            });

        } catch (Exception e){
            con.backendDisconnect(e.toString());
        }
        return throughConnection[0];
    }
}
