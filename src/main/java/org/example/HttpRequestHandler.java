package org.example;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private  final String remoteHost;
    private final int remotePort;
    private  final String proxyHost;
    private final int proxyPort;


    public HttpRequestHandler(String remoteHost, int remotePort, String proxyHost, int proxyPort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
        request.headers().set(HttpHeaderNames.HOST, remoteHost);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

        ctx.writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        System.out.println("Proxy is working for "+remoteHost+"! > "+proxyHost+":"+proxyPort);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //System.out.println("Proxy doesn't work for "+remoteHost+"! > "+proxyHost+":"+proxyPort);

        ctx.close();
    }
}
