package org.example;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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


        List<ProxyModel> proxies = ProxyDownloader.getProxies();

        for(ProxyModel proxyModel : proxies){
            ProxyClient proxyClient = new ProxyClient(
                    proxyModel.getIp(),
                    proxyModel.getPort(),
                    "example.com",
                    443,
                    proxyModel.getType());
            proxyClient.setSslContext(sslContext);

            proxyClient.testProxy();

        }

       /* try (InputStream inputStream = Main.class.getResourceAsStream("/httpProxy.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String ipAddress = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    //String username = parts[2];
                    //String password = parts[3];




                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

  /*  public static void testHttp(){
        try (InputStream inputStream = Main.class.getResourceAsStream("/httpProxy.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String ipAddress = parts[0];
                    int port = Integer.parseInt(parts[1]);

                    HttpsProxyClient client = HttpsProxyClient.builder()
                            .proxyHost(ipAddress)
                            .proxyPort(port)
                            .remoteHost("example.org")
                            .remotePort(80)
                            .sslContext(null)
                            .build();

                    client.testProxy();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    /*public  static void testSocks4(){
        try (InputStream inputStream = Main.class.getResourceAsStream("/httpProxy.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String ipAddress = parts[0];
                    int port = Integer.parseInt(parts[1]);

                    ProxyClient client = ProxyClient.builder()
                        .proxyHost(ipAddress)
                        .proxyPort(port)
                        .remoteHost("example.org")
                        .remotePort(80)
                        .sslContext(null)
                        .build();

                    client.testProxy();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

}