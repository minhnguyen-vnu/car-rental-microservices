package com.paymentservice.kernel.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VnPaySigner {
    private VnPaySigner(){}

    public static String buildQueryString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
            String v = params.get(k);
            if (v == null || v.isEmpty()) continue;
            if (!sb.isEmpty()) sb.append('&');
            sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(v, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    public static String hmacSHA512(String secret, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] raw = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }

    public static String sign(Map<String, String> params, String secret) {
        Map<String,String> copy = new HashMap<>(params);
        copy.remove("vnp_SecureHash");
        copy.remove("vnp_SecureHashType");
        String data = buildQueryString(copy);
        return hmacSHA512(secret, data);
    }

    public static boolean verify(Map<String, String> params, String secret) {
        String given = params.getOrDefault("vnp_SecureHash", "");
        Map<String,String> copy = new HashMap<>(params);
        copy.remove("vnp_SecureHash");
        copy.remove("vnp_SecureHashType");
        String data = buildQueryString(copy);
        String expect = hmacSHA512(secret, data);
        return expect.equalsIgnoreCase(given);
    }
}
