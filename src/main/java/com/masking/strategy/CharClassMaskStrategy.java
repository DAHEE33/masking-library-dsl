package com.masking.strategy;

import java.util.*;

public class CharClassMaskStrategy implements MaskStrategy {
    private final Set<CharClass> targetClasses;
    private final char maskChar;

    private CharClassMaskStrategy(Set<CharClass> targetClasses, char maskChar) {
        this.targetClasses = targetClasses;
        this.maskChar = maskChar;
    }

    public static CharClassMaskStrategy of(Set<CharClass> targetClasses, char maskChar) {
        return new CharClassMaskStrategy(targetClasses, maskChar);
    }

    @Override
    public String mask(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (targetClasses.contains(CharClass.of(ch))) {
                sb.append(maskChar);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}

