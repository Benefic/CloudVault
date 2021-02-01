package ru.abenefic.cloudvault.server.storage;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.auth.AuthorisationException;
import ru.abenefic.cloudvault.common.commands.DirectoryTree;
import ru.abenefic.cloudvault.common.commands.FilePart;
import ru.abenefic.cloudvault.common.commands.FilesList;
import ru.abenefic.cloudvault.common.commands.StringData;
import ru.abenefic.cloudvault.server.model.User;

import java.io.FileInputStream;
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

                        InputStream inputStream = new FileInputStream(filePath.toAbsolutePath().toString());
                        while ((read = inputStream.read(buffer)) != -1) {

                            response = Command.filePartTransferCommand(fileName, buffer, read, false);
                            ctx.writeAndFlush(response);
                        }
                        response = Command.filePartTransferCommand(fileName, buffer, read, true);
                        ctx.writeAndFlush(response);
                    }
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
