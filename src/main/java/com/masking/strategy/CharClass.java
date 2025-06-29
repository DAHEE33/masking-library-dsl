package com.masking.strategy;

public enum CharClass {
    LETTER, DIGIT, HANGUL, SPACE, OTHER;

    public static CharClass of(char ch) {
        if (Character.isLetter(ch)) return LETTER;
        if (Character.isDigit(ch)) return DIGIT;
        if (String.valueOf(ch).matches("[\\uac00-\\ud7af]")) return HANGUL;
        if (Character.isWhitespace(ch)) return SPACE;
        return OTHER;
    }
}

