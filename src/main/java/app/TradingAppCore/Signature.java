package app.TradingAppCore;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Signature {


    public static String generateSignature(String apiSecret, String verb, String path, long expires, String data)
            throws Exception {
        String message = verb + path + expires + data;
        byte[] hash = hmacSha256(apiSecret.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static byte[] hmacSha256(byte[] key, byte[] message) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        sha256HMAC.init(secretKey);
        return sha256HMAC.doFinal(message);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static long createExpires() {
        return System.currentTimeMillis() / 1000 + 300;
    }
}
