package com.masking.action;

import com.masking.strategy.mask.MaskStrategy;

import java.util.Map;

/**
 * 	특정 필드에 단일 마스킹 전략 적용
 */
public class MaskAction implements Action{

    private final String field;
    private final MaskStrategy strategy;

    /**
     * MaskAction 인스턴스를 생성합니다.
     * @param field 마스킹할 필드명
     * @param strategy 마스킹 전략
     */
    private MaskAction(String field, MaskStrategy strategy) {
        this.field = field;
        this.strategy = strategy;
    }

    /**
     * MaskAction 인스턴스를 생성합니다.
     * @param field 마스킹할 필드명
     * @param strategy 마스킹 전략
     * @return MaskAction 인스턴스
     */
    public static MaskAction of(String field, MaskStrategy strategy) {
        return new MaskAction(field, strategy);
    }

    /**
     * 레코드에 마스킹을 적용합니다.
     * @param record 처리할 레코드
     */
    @Override
    public void apply(Map<String, String> record) {
        String value = record.get(field);
        if (value != null) {
            record.put(field, strategy.mask(value));
        }
    }
    
    /**
     * 마스킹 대상 필드명을 반환합니다.
     * @return 필드명
     */
    public String getField() {
        return field;
    }
    
    /**
     * 마스킹 전략을 반환합니다.
     * @return 마스킹 전략
     */
    public MaskStrategy getStrategy() {
        return strategy;
    }
}
