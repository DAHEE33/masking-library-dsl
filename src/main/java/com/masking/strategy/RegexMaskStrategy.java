package com.masking.strategy;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMaskStrategy implements MaskStrategy {
    private final Pattern pattern;
    private final char maskChar;

    private RegexMaskStrategy(Pattern pattern, char maskChar) {
        this.pattern = pattern;
        this.maskChar = maskChar;
    }

    public static RegexMaskStrategy of(String regex, char maskChar) {
        return new RegexMaskStrategy(Pattern.compile(regex), maskChar);
    }

    @Override
    public String mask(String input) {
        if (input == null) return null;
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group();
            char[] maskArr = new char[group.length()];
            Arrays.fill(maskArr, maskChar);
            String replacement = Matcher.quoteReplacement(new String(maskArr));
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
