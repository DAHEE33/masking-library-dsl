package com.masking.step;

import java.util.Map;

/**
 * 문자열 필드를 prefix/suffix만 남기고 마스킹 처리
 */
public class MaskFieldStep implements Step {

    /**처리할 map의 key*/
    private final String fieldName;
    private final int prefixLength;
    private final int suffixLength;
    /**마스킹에 사용할 문자*/
    private final char maskChar;

    private MaskFieldStep(String fieldName, int prefixLength, int suffixLength, char maskChar) {
        this.fieldName = fieldName;
        this.prefixLength = prefixLength;
        this.suffixLength = suffixLength;
        this.maskChar = maskChar;
    }

    public static MaskFieldStep of(String fieldName, int prefixLength, int suffixLength, char maskChar) {
        return new MaskFieldStep(fieldName, prefixLength, suffixLength, maskChar);
    }

    @Override
    public void process(Map<String, Object> record) {
        Object value = record.get(fieldName);
        if (!(value instanceof String)) return;

        String s = (String) value;
        int len = s.length();
        if (len <= prefixLength + suffixLength) {
            // 패턴보다 짧으면 전체 마스킹
            record.put(fieldName, maskCharRepeat(len));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(s, 0, prefixLength);
            sb.append(maskCharRepeat(len - prefixLength - suffixLength));
            sb.append(s, len - suffixLength, len);
            record.put(fieldName, sb.toString());
        }
    }

    private String maskCharRepeat(int count) {
        return String.valueOf(maskChar).repeat(Math.max(0, count));
    }
}
