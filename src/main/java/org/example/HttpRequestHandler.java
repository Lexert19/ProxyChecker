package org.example;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private  final String remoteHost;
    private final int remotePort;
    private final ProxyModel proxy;


    public HttpRequestHandler(String remoteHost, int remotePort, ProxyModel proxy) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.proxy = proxy;


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
        request.headers().set(HttpHeaderNames.HOST, remoteHost);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

        ctx.writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
        System.out.println(proxy.toString());
        SaveProxy.appendProxyToFile(proxy);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
