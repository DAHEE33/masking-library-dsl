package com.masking.strategy.tokenize;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 입력값이 같으면 토큰도 같아야 하고, salt를 통해 보안성을 추가하고 싶을 때
 *
 * MessageDigest : 자바에서 제공하는 해시 알고리즘(MD5, SHA-1, SHA-256 등)을 쉽게 쓸 수 있도록 래핑해 놓은 클래스
 *
 */
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
            md.update(salt.getBytes()); // 해시 입력 데이터를 버퍼에 추가
            byte[] digest = md.digest(input.getBytes()); // 버퍼 + 주어진 입력을 합쳐 최종 해시(바이트 배열) 생성
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
