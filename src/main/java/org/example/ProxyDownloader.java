package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

public class ProxyDownloader {
    private static final String API_URL = "https://proxylist.geonode.com/api/proxy-list?limit=500&page=1&sort_by=lastChecked&sort_type=desc";

    public static List<ProxyModel> getProxies() {
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
