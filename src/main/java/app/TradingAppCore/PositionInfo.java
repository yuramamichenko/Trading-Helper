package app.TradingAppCore;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PositionInfo {
    private User user;
    private String symbol;

    private HttpResponse requestPositionsInfo() {
        String data = "";
        long expires = Signature.createExpires();
        HttpResponse<String> response = null;
        try {
            String signature = Signature.generateSignature(user.getSecret(), "GET", "/api/v1/position", expires, data);
            HttpRequest requestToGetPosition = HttpRequest.newBuilder()
                    .uri(URI.create(user.getBaseUrl() + "/api/v1/position"))
                    .header("Content-Type", "application/json")
                    .header("api-key", user.getKey())
                    .header("api-expires", String.valueOf(expires))
                    .header("api-signature", signature)
                    .GET()
                    .build();
            response = user.getClient().send(requestToGetPosition, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
        }
        return response;
    }

    public String getCurrentPnlByTicker() {
        HttpResponse response = requestPositionsInfo();
        if (response != null && response.statusCode() == 200) {
            return KeyResponseParser.parseInfoByTicker(response, "unrealisedPnl", symbol);
        } else {
            return "0";
        }
    }

    public String getPosCommByTicker() {
        HttpResponse response = requestPositionsInfo();
        if (response != null && response.statusCode() == 200) {
            String comm = KeyResponseParser.parseInfoByTicker(response, "posComm", symbol);
//            System.out.println(comm);
            return comm;
        } else {
            return "0";
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
