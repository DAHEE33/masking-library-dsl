package com.masking.action;

import com.masking.strategy.tokenize.TokenizationStrategy;

import java.util.Map;

public class TokenizeAction implements Action {
    private final String field;
    private final TokenizationStrategy strategy;

    private TokenizeAction(String field, TokenizationStrategy strategy) {
        this.field = field;
        this.strategy = strategy;
    }

    /**
     * TokenizeAction 인스턴스를 생성합니다.
     * @param field 토큰화할 필드명
     * @param strategy 토큰화 전략
     * @return TokenizeAction 인스턴스
     */
    public static TokenizeAction of(String field, TokenizationStrategy strategy) {
        return new TokenizeAction(field, strategy);
    }

    /**
     * 레코드에 토큰화를 적용합니다.
     * @param record 처리할 레코드
     */
    @Override
    public void apply(Map<String, String> record) {
        String value = record.get(field);
        if (value != null) {
            record.put(field, strategy.tokenize(value));
        }
    }
    
    /**
     * 토큰화 대상 필드명을 반환합니다.
     * @return 필드명
     */
    public String getField() {
        return field;
    }
    
    /**
     * 토큰화 전략을 반환합니다.
     * @return 토큰화 전략
     */
    public TokenizationStrategy getStrategy() {
        return strategy;
    }
}
