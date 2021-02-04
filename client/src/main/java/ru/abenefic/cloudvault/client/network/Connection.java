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
import ru.abenefic.cloudvault.common.commands.FilePart;
import ru.abenefic.cloudvault.common.commands.StringData;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Синглтон для всей работы с сетью
 */
public class Connection {

    private static final Logger LOG = LogManager.getLogger(Connection.class);

    private static Connection instance;

    private CommandCallback commandCallback; // здесь слушаем ответы сервера
    private OnConnectedCallback onConnectedCallback; // здесь говорим об успешном соединении
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
            // когда соединение установлено, менем pipeline - другой объект может захотеть слушать сервер
            // после авторизации переключаем слушателя из основного окна
            context.pipeline().removeLast();
            context.pipeline().addLast(new CommandHandler(commandCallback, onConnectedCallback));
        }
        return this;
    }

    public Connection onCommand(CommandCallback commandCallback) {
        this.commandCallback = commandCallback;
        if (context != null) {
            // см. выше
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

    public void getDirectoryTree() {
        Command command = Command.getTreeCommand();
        writeToken(command);
        context.writeAndFlush(command);
    }

    private void writeToken(Command command) {
        command.setToken(Context.current().getToken());
    }

    public void getFilesList(String path) {
        Command command = Command.getFilesCommand(new StringData(path));
        writeToken(command);
        context.writeAndFlush(command);
    }

    // команда для остановки потока с соединением при закрытии окон GUI
    public void shutdown() {
        LOG.info("shutdown");
        context.close();
    }

    public void getFile(String path) {
        Command command = Command.getFileCommand(new StringData(path));
        writeToken(command);
        context.writeAndFlush(command);
    }

    public void uploadFile(String path, String directory) {
        LOG.info("Uploading " + path);
        Path filePath = Paths.get(path); // пас для чтения

        String fileName = Paths.get(directory, filePath.getFileName().toString()).toString(); // путь для записи на сервере

        if (Files.exists(filePath)) {
            byte[] buffer = new byte[FilePart.partSize];
            Command response;

            int read;

            try (InputStream inputStream = new FileInputStream(filePath.toAbsolutePath().toString())) {
                int partNumber = 0;
                read = inputStream.read(buffer);
                while (read != -1) {
                    // на сервере прогресс не нужен, 0 заглушим
                    response = Command.filePartTransferCommand(fileName, buffer, read, false, 0, partNumber);
                    response.setToken(Context.current().getToken());
                    context.writeAndFlush(response);
                    partNumber++;
                    // TODO если сделать так: while ( (read = inputStream.read(buffer)) != -1 )
                    //  то во всех порциях окажется одинаковый массив байт... Не понимаю, на сервере работает
                    buffer = new byte[FilePart.partSize];
                    read = inputStream.read(buffer);
                }
                response = Command.filePartTransferCommand(fileName, buffer, read, true, 0, partNumber);
                response.setToken(Context.current().getToken());
                context.writeAndFlush(response);
            } catch (Exception e) {
                LOG.error("File upload", e);
            }
        }
    }


    public void removeFile(String path) {
        Command command = Command.fileRemoveCommand(path);
        writeToken(command);
        context.writeAndFlush(command);
    }

    public void renameFile(String path, String newName) {
        Command command = Command.fileRenameCommand(path, newName);
        writeToken(command);
        context.writeAndFlush(command);
    }

    public void createFolder(String path) {
        Command command = Command.createFolderCommand(path);
        writeToken(command);
        context.writeAndFlush(command);
    }


}
