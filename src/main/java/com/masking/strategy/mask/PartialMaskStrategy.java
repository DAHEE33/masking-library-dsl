package com.masking.strategy.mask;

import java.util.Arrays;

/**
 * 고정된 길이 마스킹화
 */
public class PartialMaskStrategy implements MaskStrategy {
    private final int prefixKeep;
    private final int suffixKeep;
    private final char maskChar;

    private PartialMaskStrategy(int prefixKeep, int suffixKeep, char maskChar) {
        this.prefixKeep = prefixKeep;
        this.suffixKeep = suffixKeep;
        this.maskChar = maskChar;
    }

    public static PartialMaskStrategy of(int prefixKeep, int suffixKeep, char maskChar) {
        return new PartialMaskStrategy(prefixKeep, suffixKeep, maskChar);
    }


    @Override
    public String mask(String input) {
        if (input == null) return null;
        int length = input.length();
        if (length <= prefixKeep + suffixKeep) {
            char[] fullMask = new char[length];
            Arrays.fill(fullMask, maskChar);
            return new String(fullMask);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(input, 0, prefixKeep);
        int maskLen = length - prefixKeep - suffixKeep;
        char[] maskArr = new char[maskLen];
        Arrays.fill(maskArr, maskChar);
        sb.append(maskArr);
        sb.append(input, length - suffixKeep, length);
        return sb.toString();
    }
}
