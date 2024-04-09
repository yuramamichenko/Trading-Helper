package app.TradingAppCore;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Controller {
//    public static void main(String[] args) {
//        User u = new User();
//        u.setKey("3Mwsofre7yG7aOc1NHB8iLHl");
//        u.setSecret("Wy_ru4DAPSfxwVjIS3cPiT9Va9wPEDFv2-EtvxWNlA2MjRym");
//        System.out.println(new Controller().getUserAccountInfo(u));
//    }
    private User user;
    private String symbol, side;
    private int quantity;
    private int buysCount;

    public HttpResponse<String> createMarketOrder() {
        HttpResponse<String> response = null;
        try {
            RequestData data = new RequestData();
            data.addValues("symbol", symbol);
            data.addValues("side", side);
            data.addValues("orderQty", String.valueOf(quantity));

            long expires = Signature.createExpires();
            String signPost = Signature.generateSignature(user.getSecret(), "POST", "/api/v1/order", expires, String.valueOf(data));
            HttpRequest requestToBuy = HttpRequest.newBuilder()
                    .uri(URI.create(user.getBaseUrl() + "/api/v1/order"))
                    .header("Content-Type", "application/json")
                    .header("api-key", user.getKey())
                    .header("api-expires", String.valueOf(expires))
                    .header("api-signature", signPost)
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(data)))
                    .build();
            response = user.getClient().send(requestToBuy, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Open price = " + KeyResponseParser.parseInfo(response, "avgPx"));
            System.out.println(response.body());
        } catch (Exception ignored) {
        }
        return response;
    }

    public HttpResponse<String> createPartMarketOrder(String side, int quantity) {
        HttpResponse<String> response = null;
        try {
            RequestData data = new RequestData();
            data.addValues("symbol", symbol);
            data.addValues("side", side);
            data.addValues("orderQty", String.valueOf(quantity));

            long expires = Signature.createExpires();
            String signPost = Signature.generateSignature(user.getSecret(), "POST", "/api/v1/order", expires, String.valueOf(data));
            HttpRequest requestToBuy = HttpRequest.newBuilder()
                    .uri(URI.create(user.getBaseUrl() + "/api/v1/order"))
                    .header("Content-Type", "application/json")
                    .header("api-key", user.getKey())
                    .header("api-expires", String.valueOf(expires))
                    .header("api-signature", signPost)
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(data)))
                    .build();
            response = user.getClient().send(requestToBuy, HttpResponse.BodyHandlers.ofString());
//            System.out.println(side + KeyResponseParser.parseInfo(response, "avgPx"));
            System.out.println(response.body());
        } catch (Exception ignored) {
        }
        return response;
    }
    public HttpResponse<String> closePositionByTicker() {
        HttpResponse<String> response = null;
        try {
            RequestData data = new RequestData();
            data.addValues("symbol", symbol);
            data.addValues("execInst", "Close");

            long expires = Signature.createExpires();
            String signature = Signature.generateSignature(user.getSecret(), "POST", "/api/v1/order", expires, String.valueOf(data));
            HttpRequest requestToClose = HttpRequest.newBuilder()
                    .uri(URI.create(user.getBaseUrl() + "/api/v1/order"))
                    .header("Content-Type", "application/json")
                    .header("api-key", user.getKey())
                    .header("api-expires", String.valueOf(expires))
                    .header("api-signature", signature)
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(data)))
                    .build();
            response = user.getClient().send(requestToClose, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (Exception ignored) {
        }
        return response;
    }

    public HttpResponse<String> getUserAccountInfo() {
        HttpResponse<String> response = null;
        try {
            long expires = Signature.createExpires();
            String signGet = Signature.generateSignature(user.getSecret(), "GET", "/api/v1/user", expires, "");
            HttpRequest requestToBuy = HttpRequest.newBuilder()
                    .uri(URI.create(user.getBaseUrl() + "/api/v1/user"))
                    .header("Content-Type", "application/json")
                    .header("api-key", user.getKey())
                    .header("api-expires", String.valueOf(expires))
                    .header("api-signature", signGet)
                    .GET()
                    .build();
            response = user.getClient().send(requestToBuy, HttpResponse.BodyHandlers.ofString());
//            System.out.println(response.body());
        } catch (Exception ignored) {
        }
        return response;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getBuysCount() {
        return buysCount;
    }

    public void setBuysCount(int buysCount) {
        this.buysCount = buysCount;
    }
}