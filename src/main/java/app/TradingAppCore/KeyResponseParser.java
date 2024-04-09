package app.TradingAppCore;

import java.net.http.HttpResponse;

public class KeyResponseParser {
    public static String parseInfo(HttpResponse response, String key) {
        String info = "ERROR";
        try {
            String[] strings = response.body().toString().split(",");
            for (String s : strings) {
                s = s.replaceAll("[{}\"]", "");
                String[] parts = s.split(":");
                if (parts[0].toLowerCase().equals(key.toLowerCase())) {
                    info = parts[1];
                    break;
                }
            }
            if (info.equals("null")) {
                info = "0";
            }
        } catch (Exception ignored) {
        }
        return info;
    }

    public static String parseInfoByTicker(HttpResponse response, String key, String ticker) {
        String info = "";
        try {
            int index = response.body().toString().indexOf("\"symbol\":\"" + ticker + "\"");
            String str = response.body().toString().substring(index);
            String[] strings = str.split(",");
            for (String s : strings) {
                s = s.replaceAll("[{}\"]", "");
                String[] parts = s.split(":");
                if (parts[0].toLowerCase().equals(key.toLowerCase())) {
                    info = parts[1];
                    break;
                }
            }
            if (info.equals("null")) {
                info = "0";
            }
        } catch (Exception ignored) {
        }
        return info;
    }
}