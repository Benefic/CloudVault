package ru.abenefic.cloudvault.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.controller.AuthDialogController;
import ru.abenefic.cloudvault.client.controller.FileManagerController;
import ru.abenefic.cloudvault.client.support.Context;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.commands.StringData;

public class Connection {

    private static final Logger LOG = LogManager.getLogger(Connection.class);

    private void initHandler(ChannelInboundHandlerAdapter handlerAdapter) {
        String host = Context.current().getServerHost();
        int port = Context.current().getServerPort();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    handlerAdapter);
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("e=", e);
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public void register(AuthDialogController handler) throws InterruptedException {
        initHandler(new AuthHandler(handler, true));
    }

    public void login(AuthDialogController handler) throws InterruptedException {
        initHandler(new AuthHandler(handler, false));
    }

    public void getDirectoryTree(FileManagerController controller) throws InterruptedException {
        initHandler(new CommandHandler(controller, Command.getTreeCommand()));
    }

    public void getFilesList(FileManagerController controller, String path) {
        initHandler(new CommandHandler(controller, Command.getFilesCommand(new StringData(path))));
    }

    public void uploadFile(FileManagerController controller, String path) {
        LOG.info("Uploading " + path);
    }
}
