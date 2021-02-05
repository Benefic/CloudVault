package ru.abenefic.cloudvault.server.storage;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.auth.AuthorisationException;
import ru.abenefic.cloudvault.common.commands.*;
import ru.abenefic.cloudvault.server.model.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@ChannelHandler.Sharable
public class CommandHandler extends SimpleChannelInboundHandler<Command> {

    private static final Logger LOG = LogManager.getLogger(CommandHandler.class);

    private final StorageServer serverContext;

    public CommandHandler(StorageServer serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        LOG.info(command);
        User user = serverContext.getUser(command.getToken());
        if (user == null) {
            LOG.error("User not registered on the server");
            ctx.writeAndFlush(new AuthorisationException("User not registered on the server"));
        } else {
            Command response;
            switch (command.getType()) {
                case GET_TREE -> {
                    DirectoryTree tree = StorageProvider.getUserTree(user);
                    response = Command.getTreeCommand();
                    response.setData(tree);
                    ctx.writeAndFlush(response);
                }
                case GET_FILES -> {
                    FilesList list = StorageProvider.getFilesList(user, ((StringData) command.getData()).getData());
                    response = Command.getFilesCommand(list);
                    ctx.writeAndFlush(response);
                }
                case GET_FILE -> {
                    Path filePath = StorageProvider.getFilePath(user, ((StringData) command.getData()).getData());
                    String fileName = filePath.getFileName().toString();

                    if (Files.exists(filePath)) {
                        byte[] buffer = new byte[FilePart.partSize];

                        int read;

                        try (InputStream inputStream = new FileInputStream(filePath.toAbsolutePath().toString())) {
                            float size = inputStream.available();
                            int iterator = 1;
                            while ((read = inputStream.read(buffer)) != -1) {
                                float progress = ((float) (buffer.length * iterator)) / size;
                                response = Command.filePartTransferCommand(fileName, buffer, read, false, progress, iterator);
                                ctx.writeAndFlush(response);
                                iterator++;
                            }
                            response = Command.filePartTransferCommand(fileName, buffer, read, true, 0, iterator);
                            ctx.writeAndFlush(response);
                        } catch (IOException e) {
                            LOG.error(e);
                        }
                    }
                }
                case FILE_TRANSFER -> {
                    boolean result = StorageProvider.writeFilePart(user, (FilePart) command.getData());
                    response = Command.fileTransferResult(result);
                    ctx.writeAndFlush(response);
                }
                case REMOVE_FILE -> {
                    Path filePath = StorageProvider.getFilePath(user, ((StringData) command.getData()).getData());
                    try {
                        Files.delete(filePath);
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                    // возвращаем команду как сигнал завершения, пусть клиент по нему обновляет таблицу у себя
                    ctx.writeAndFlush(command);
                }
                case RENAME_FILE -> {
                    RenameData data = (RenameData) command.getData();
                    Path filePath = StorageProvider.getFilePath(user, data.getFilePath());
                    Files.move(filePath, filePath.getParent().resolve(data.getNewName()));
                    // возвращаем команду как сигнал завершения, пусть клиент по нему обновляет таблицу у себя
                    ctx.writeAndFlush(command);
                }
                case CREATE_FOLDER -> {
                    String name = ((StringData) command.getData()).getData();
                    Path filePath = StorageProvider.getFilePath(user, name);
                    try {
                        Files.createDirectories(filePath);
                    } catch (IOException e) {
                        LOG.error(e);
                    }
                    ctx.writeAndFlush(command);
                }
                default -> throw new IllegalStateException("Unexpected value: " + command.getType());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("Command error", cause);
    }
}
