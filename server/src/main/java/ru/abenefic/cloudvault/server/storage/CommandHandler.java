package ru.abenefic.cloudvault.server.storage;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.auth.AuthorisationException;
import ru.abenefic.cloudvault.common.commands.DirectoryTree;
import ru.abenefic.cloudvault.server.model.User;

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
                }
                default -> throw new IllegalStateException("Unexpected value: " + command.getType());
            }
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Command error", cause);
        ctx.close();
    }
}
