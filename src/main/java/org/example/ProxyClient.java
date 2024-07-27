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
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;

@Setter
public class ProxyClient {
    protected final String proxyHost;
    protected final int proxyPort;

    protected final String remoteHost;
    protected final int remotePort;
    protected SslContext sslContext = null;
    private final String proxyUsername = "";
    private final String proxyPassword = "";
    private ProxyType type;
    private ChannelInitializer channelInitializer;


    public ProxyClient(String proxyHost, int proxyPort, String remoteHost, int remotePort, ProxyType type){
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.setType(type);
    }

    public void setType(ProxyType type){
        this.type = type;

        switch (type){
            case HTTPS -> setHttpsChannel();
            case HTTP -> setHttpsChannel();
            case SOCKS4 -> setSocks4Channel();
            case SOCKS5 -> setSocks5Channel();
        }
    }
    private void setSocks4Channel() {
        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch)  {

                ch.pipeline().addLast(
                        new Socks4ProxyHandler(
                                new InetSocketAddress(proxyHost, proxyPort)
                        )
                );

                if(sslContext != null){
                    SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                    ch.pipeline().addLast(sslHandler);
                }

                ch.pipeline().addLast( new TcpHttpRequestHandler(remoteHost, remotePort, proxyHost, proxyPort));
            }
        };
    }

    private void setSocks5Channel() {
        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch)  {

                ch.pipeline().addLast(
                        new Socks5ProxyHandler(
                                new InetSocketAddress(proxyHost, proxyPort),
                                proxyUsername,
                                proxyPassword)
                );

                if(sslContext != null){
                    SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                    ch.pipeline().addLast(sslHandler);
                }

                ch.pipeline().addLast( new TcpHttpRequestHandler(remoteHost, remotePort, proxyHost, proxyPort));
            }
        };
    }

    private void setHttpsChannel(){
        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws SSLException {

                ch.pipeline().addLast(
                        new HttpProxyHandler(
                                new InetSocketAddress(proxyHost, proxyPort), proxyUsername, proxyPassword
                        )
                );


                if(sslContext != null){
                    SslHandler sslHandler = sslContext.newHandler(ch.alloc(), remoteHost, remotePort);
                    ch.pipeline().addLast(sslHandler);
                }

                ch.pipeline().addLast(new HttpClientCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                //ch.pipeline().addLast(new HttpObjectAggregator(8192));


                ch.pipeline().addLast(new HttpRequestHandler(remoteHost, remotePort, proxyHost, proxyPort));
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
