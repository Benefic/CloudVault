package ru.abenefic.cloudvault.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.controller.FileManagerController;
import ru.abenefic.cloudvault.client.support.Context;
import ru.abenefic.cloudvault.common.Command;

public class CommandHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LogManager.getLogger(CommandHandler.class);
    private final FileManagerController controller;
    private final Command command;

    public CommandHandler(FileManagerController controller, Command command) {
        this.controller = controller;
        this.command = command;
        command.setToken(Context.current().getToken());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(command);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOG.info(msg);
        controller.onCommandSuccess((Command) msg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("Command error (client)", cause);
        ctx.close();
    }


}
