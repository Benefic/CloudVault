package ru.abenefic.cloudvault.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.common.NetworkCommand;

/**
 * Главнй по командам
 */
public class CommandHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LogManager.getLogger(CommandHandler.class);
    private final CommandCallback controller;  // здесь слушаем ответ сервера
    private final OnConnectedCallback connectedCallback; // здесь говорим об успешном подключении

    public CommandHandler(CommandCallback controller, OnConnectedCallback connectedCallback) {
        this.controller = controller;
        this.connectedCallback = connectedCallback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        connectedCallback.call();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOG.info(msg);
        controller.call((NetworkCommand) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("Command error (client)", cause);
    }


}
