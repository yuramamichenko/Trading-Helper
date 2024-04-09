package app.TradingAppCore;

import java.net.http.HttpClient;

public class User {

    private String key, secret;
    //private String baseUrl = "https://testnet.bitmex.com";
    private String baseUrl = "https://www.bitmex.com";
    private HttpClient client = HttpClient.newHttpClient();

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpClient getClient() {
        return client;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}