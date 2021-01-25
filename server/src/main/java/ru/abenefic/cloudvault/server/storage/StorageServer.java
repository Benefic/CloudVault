package ru.abenefic.cloudvault.server.storage;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.server.model.User;
import ru.abenefic.cloudvault.server.support.AuthHandler;

import java.util.concurrent.ConcurrentHashMap;

public class StorageServer {

    private static final Logger LOG = LogManager.getLogger(StorageServer.class);
    private static final ConcurrentHashMap<String, User> clients = new ConcurrentHashMap<>();

    public StorageServer(int port) {

        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        AuthHandler authHandler = new AuthHandler(this);
        CommandHandler commandHandler = new CommandHandler(this);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    authHandler,
                                    commandHandler
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            LOG.info("server started on PORT = " + port);
            future.channel().closeFuture().sync(); // block
        } catch (InterruptedException e) {
            LOG.error("e=", e);
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public void addUser(User user, String token) {
        clients.put(token, user);
    }

    public User getUser(String token) {
        return clients.get(token);
    }
}
