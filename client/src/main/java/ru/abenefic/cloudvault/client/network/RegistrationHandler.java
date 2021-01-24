package ru.abenefic.cloudvault.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.controller.AuthDialogController;
import ru.abenefic.cloudvault.client.support.Config;
import ru.abenefic.cloudvault.common.auth.Authentication;
import ru.abenefic.cloudvault.common.auth.AuthorisationException;

public class RegistrationHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LogManager.getLogger(RegistrationHandler.class);
    private final AuthDialogController handler;

    public RegistrationHandler(AuthDialogController handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Authentication reg = new Authentication();
        reg.setLogin(Config.current().getLogin());
        reg.setPassword(Config.current().getPassword());
        reg.setRegistration(true);
        ctx.writeAndFlush(reg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof AuthorisationException) {
            handler.fireError(((AuthorisationException) msg).getMessage());
        } else {
            //Authentication auth = (Authentication) msg;
            handler.loginSuccess();
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Register error", cause);
    }
}
