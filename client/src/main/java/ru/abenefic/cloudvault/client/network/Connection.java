package ru.abenefic.cloudvault.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
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
import ru.abenefic.cloudvault.client.support.Context;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.auth.Authentication;
import ru.abenefic.cloudvault.common.commands.StringData;

public class Connection {

    private static final Logger LOG = LogManager.getLogger(Connection.class);

    private static Connection instance;

    private CommandCallback commandCallback;
    private OnConnectedCallback onConnectedCallback;
    private SocketChannel context;

    private Connection() {
    }

    public static Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    public Connection onConnected(OnConnectedCallback onConnectedCallback) {
        this.onConnectedCallback = onConnectedCallback;
        if (context != null) {
            context.pipeline().removeLast();
            context.pipeline().addLast(new CommandHandler(commandCallback, onConnectedCallback));
        }
        return this;
    }

    public Connection onCommand(CommandCallback commandCallback) {
        this.commandCallback = commandCallback;
        if (context != null) {
            context.pipeline().removeLast();
            context.pipeline().addLast(new CommandHandler(commandCallback, onConnectedCallback));
        }
        return this;
    }

    public void connect() {
        if (onConnectedCallback == null) {
            throw new IllegalStateException("OnConnectedCallback is null!");
        }
        if (commandCallback == null) {
            throw new IllegalStateException("CommandCallback is null!");
        }

        String host = Context.current().getServerHost();
        int port = Context.current().getServerPort();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) {
                            context = channel;
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new CommandHandler(commandCallback, onConnectedCallback));
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("e=", e);
        } finally {
            LOG.info("shutdownGracefully");
            workerGroup.shutdownGracefully();
        }

    }

    public void register() {
        Authentication reg = new Authentication();
        reg.setLogin(Context.current().getLogin());
        reg.setPassword(Context.current().getPassword());
        reg.setRegistration(true);
        context.writeAndFlush(reg);
    }

    public void login() {
        Authentication reg = new Authentication();
        reg.setLogin(Context.current().getLogin());
        reg.setPassword(Context.current().getPassword());
        context.writeAndFlush(reg);
    }

    public void getDirectoryTree() throws InterruptedException {
        Command command = Command.getTreeCommand();
        command.setToken(Context.current().getToken());
        context.writeAndFlush(command);
    }

    public void getFilesList(String path) {
        Command command = Command.getFilesCommand(new StringData(path));
        command.setToken(Context.current().getToken());
        context.writeAndFlush(command);
    }

    public void shutdown() {
        LOG.info("shutdown");
        context.close();
    }

    public void getFile(String path) {
        Command command = Command.getFileCommand(new StringData(path));
        command.setToken(Context.current().getToken());
        context.writeAndFlush(command);
    }

    public void uploadFile(String path) {
        LOG.info("Uploading " + path);
    }


}
