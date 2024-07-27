package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

class TcpHttpRequestHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private  final String remoteHost;
    private final int remotePort;
    private  final String proxyHost;
    private final int proxyPort;


    public TcpHttpRequestHandler(String remoteHost, int remotePort, String proxyHost, int proxyPort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        String httpRequest = "GET / HTTP/1.1\r\n" +
                "Host: "+remoteHost+"\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n" +
                "Accept-Language: en-US,en;q=0.5\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        ByteBuf requestBuffer = Unpooled.copiedBuffer(httpRequest, CharsetUtil.UTF_8);
        ctx.writeAndFlush(requestBuffer);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        //System.out.println("Received data from server: " + msg.toString(CharsetUtil.UTF_8).length() );
        System.out.println("Proxy is working for "+remoteHost+"! > "+proxyHost+":"+proxyPort);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        //cause.printStackTrace();
        ctx.close();
    }
}
