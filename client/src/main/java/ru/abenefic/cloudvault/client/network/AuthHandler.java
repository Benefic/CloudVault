package ru.abenefic.cloudvault.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.controller.AuthDialogController;
import ru.abenefic.cloudvault.client.support.Context;
import ru.abenefic.cloudvault.common.auth.Authentication;
import ru.abenefic.cloudvault.common.auth.AuthorisationException;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LogManager.getLogger(AuthHandler.class);
    private final AuthDialogController handler;
    private final boolean registration;

    public AuthHandler(AuthDialogController handler, boolean registration) {
        super();
        this.handler = handler;
        this.registration = registration;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Authentication reg = new Authentication();
        reg.setLogin(Context.current().getLogin());
        reg.setPassword(Context.current().getPassword());
        reg.setRegistration(registration);
        ctx.writeAndFlush(reg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof AuthorisationException) {
            handler.fireError(((AuthorisationException) msg).getMessage());
        } else {
            handler.loginSuccess();
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Register error", cause);
    }
}
