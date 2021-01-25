package ru.abenefic.cloudvault.server.support;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.common.auth.Authentication;
import ru.abenefic.cloudvault.common.auth.AuthorisationException;
import ru.abenefic.cloudvault.server.model.User;
import ru.abenefic.cloudvault.server.storage.StorageServer;

import java.util.UUID;

@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<Authentication> {

    private static final Logger LOG = LogManager.getLogger(AuthHandler.class);
    private final StorageServer serverContext;

    public AuthHandler(StorageServer serverContext) {
        this.serverContext = serverContext;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Authentication msg) throws Exception {
        try {
            User user;
            if (msg.isRegistration()) {
                user = UserService.instance().register(msg);
            } else {
                user = UserService.instance().authorize(msg);
            }
            String token = UUID.randomUUID().toString();
            msg.setToken(token);
            msg.setUserId(user.getId());
            serverContext.addUser(user, token);
            ctx.writeAndFlush(msg);
        } catch (AuthorisationException e) {
            ctx.writeAndFlush(e);
            LOG.error("Auth", e);
        }
    }
}
