package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import lombok.Setter;

import java.net.InetSocketAddress;

@Setter
public class ProxyClient {

    private final String remoteHost;
    private final int remotePort;
    private SslContext sslContext = null;
    private final String proxyUsername = "";
    private final String proxyPassword = "";
    private ChannelInitializer<SocketChannel> channelInitializer;
    private final ProxyModel proxy;


    public ProxyClient(String remoteHost, int remotePort, ProxyModel proxy){

        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.proxy = proxy;
        this.setChannel();
    }

    public void setChannel(){

        switch (proxy.getType()){
            case HTTPS, HTTP -> setHttpsChannel();
            case SOCKS4 -> setSocks4Channel();
            case SOCKS5 -> setSocks5Channel();
        }
    }
    private void setSocks4Channel() {
        channelInitializer = new ChannelInitializer<>() {
            @Override
            public void initChannel(SocketChannel ch)  {

                ch.pipeline().addLast(
                        new Socks4ProxyHandler(
                                new InetSocketAddress(proxy.getIp(), proxy.getPort())
                        )
                );

                if(sslContext != null){
                    SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                    ch.pipeline().addLast(sslHandler);
                }

                ch.pipeline().addLast( new TcpHttpRequestHandler(remoteHost, remotePort, proxy));
            }
        };
    }

    private void setSocks5Channel() {
        channelInitializer = new ChannelInitializer<>() {
            @Override
            public void initChannel(SocketChannel ch)  {

                ch.pipeline().addLast(
                        new Socks5ProxyHandler(
                                new InetSocketAddress(proxy.getIp(), proxy.getPort()),
                                proxyUsername,
                                proxyPassword)
                );

                if(sslContext != null){
                    SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                    ch.pipeline().addLast(sslHandler);
                }

                ch.pipeline().addLast( new TcpHttpRequestHandler(remoteHost, remotePort, proxy));
            }
        };
    }

    private void setHttpsChannel(){
        channelInitializer = new ChannelInitializer<>() {
            @Override
            public void initChannel(SocketChannel ch) {

                ch.pipeline().addLast(
                        new HttpProxyHandler(
                                new InetSocketAddress(proxy.getIp(), proxy.getPort()), proxyUsername, proxyPassword
                        )
                );


                if(sslContext != null){
                    SslHandler sslHandler = sslContext.newHandler(ch.alloc(), remoteHost, remotePort);
                    ch.pipeline().addLast(sslHandler);
                }

                ch.pipeline().addLast(new HttpClientCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                //ch.pipeline().addLast(new HttpObjectAggregator(8192));


                ch.pipeline().addLast(new HttpRequestHandler(remoteHost, remotePort,proxy));
            }
        };
    }

    public void testProxy(){



        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    //.option(ChannelOption.SO_RCVBUF, 1024)
                    .handler(channelInitializer);


            ChannelFuture f = bootstrap.connect(remoteHost, remotePort);
            f.channel().closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });

        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
