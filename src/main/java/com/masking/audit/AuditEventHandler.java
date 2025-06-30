package com.masking.audit;

public interface AuditEventHandler {
    /**
     * 필드별 적용 전후 값을 전달받아 처리
     * @param field   대상 필드 이름
     * @param before  액션 적용 전 값
     * @param after   액션 적용 후 값
     */
    void handle(String field, String before, String after);
}
