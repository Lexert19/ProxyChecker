package org.example;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.util.LinkedList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws SSLException {
        testHttps();
    }

    public static void testHttps() throws SSLException {

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();


        List<ProxyModel> proxies = new LinkedList<>();

        List<ProxyModel> geosProxies = new LinkedList<>();

        for(int i=0; i<5; i++){
            geosProxies.addAll(ProxyDownloader.getProxiesGeoNode(i+1));
        }
        System.out.println("number of proxies in Geo: "+ geosProxies.size());
        proxies.addAll(geosProxies);


        List<ProxyModel> proxyScrapesProxies = ProxyDownloader.getProxiesProxyScrape();
        System.out.println("number of proxies in ProxyScrape: "+proxyScrapesProxies.size());
        proxies.addAll(proxyScrapesProxies);

        for(ProxyModel proxyModel : proxies){
            ProxyClient proxyClient = new ProxyClient(
                    "example.com",
                    443,
                    proxyModel);
            proxyClient.setSslContext(sslContext);

            proxyClient.testProxy();

        }

    }

}