package ru.abenefic.cloudvault.server.storage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.abenefic.cloudvault.common.auth.Authentication;

public class AuthHandler extends SimpleChannelInboundHandler<Authentication> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Authentication msg) throws Exception {

    }
}
