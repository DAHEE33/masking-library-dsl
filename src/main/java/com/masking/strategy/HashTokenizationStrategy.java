package com.masking.strategy;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashTokenizationStrategy implements TokenizationStrategy {
    private final String salt;

    private HashTokenizationStrategy(String salt) {
        this.salt = salt;
    }

    public static HashTokenizationStrategy of(String salt) {
        return new HashTokenizationStrategy(salt);
    }

    @Override
    public String tokenize(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
