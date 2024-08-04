package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

public class ProxyDownloader {


    public static List<ProxyModel> getProxiesProxyScrape() {
        String url = "https://api.proxyscrape.com/v3/free-proxy-list/get?request=displayproxies&proxy_format=protocolipport&format=json";
        List<ProxyModel> proxies = new LinkedList<>();


        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonString = response.body();
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray proxiesJson = jsonObject.getJSONArray("proxies");

                for (int i = 0; i < proxiesJson.length(); i++) {
                    JSONObject proxy = proxiesJson.getJSONObject(i);
                    String ip = proxy.getString("ip");
                    int port = proxy.getInt("port");
                    String protocol = proxy.getString("protocol");

                    ProxyModel proxyModel = new ProxyModel(ip, port, ProxyType.fromString(protocol));
                    proxies.add(proxyModel);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return proxies;
    }

    public static List<ProxyModel> getProxiesGeoNode(int page) {
        String API_URL = "https://proxylist.geonode.com/api/proxy-list?limit=500&page="+page+"&sort_by=lastChecked&sort_type=desc";


        List<ProxyModel> proxies = new LinkedList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonString = response.body();
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray dataArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject proxy = dataArray.getJSONObject(i);
                    String ip = proxy.getString("ip");
                    int port = proxy.getInt("port");
                    JSONArray protocols = proxy.getJSONArray("protocols");

                    ProxyModel proxyModel = new ProxyModel(ip, port, ProxyType.fromString(protocols.getString(0)) );
                    proxies.add(proxyModel);
                }
            } else {
                System.out.println("Failed to fetch data. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proxies;
    }
}
