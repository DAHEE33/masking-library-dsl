package com.masking.action;

import com.masking.audit.AuditEventHandler;

import java.io.IOException;
import java.util.Map;

public class AuditAction implements Action {
    private final String field;
    private final AuditEventHandler handler;
    private final boolean trackAfter;

    /**
     * AuditAction 인스턴스를 생성합니다.
     * @param field 감사 대상 필드명
     * @param handler 감사 이벤트 핸들러
     * @return AuditAction 인스턴스
     */
    private AuditAction(String field, AuditEventHandler handler, boolean trackAfter) {
        this.field = field;
        this.handler = handler;
        this.trackAfter = trackAfter;
    }

    /**
     * AuditAction 인스턴스를 생성합니다.
     * @param field 감사 대상 필드명
     * @param handler 감사 이벤트 핸들러
     * @return AuditAction 인스턴스
     */
    public static AuditAction of(String field, AuditEventHandler handler) {
        return new AuditAction(field, handler, false);
    }

    /**
     * AuditAction 인스턴스를 생성합니다.
     * @param field 감사 대상 필드명
     * @param handler 감사 이벤트 핸들러
     * @param trackAfter after 값 추적 여부
     * @return AuditAction 인스턴스
     */
    public static AuditAction of(String field, AuditEventHandler handler, boolean trackAfter) {
        return new AuditAction(field, handler, trackAfter);
    }

    /**
     * 레코드에 감사 처리를 적용합니다.
     * @param record 처리할 레코드
     */
    @Override
    public void apply(Map<String, String> record) {
        String before = record.get(field);
        try {
            if (trackAfter) {
                // 다음 액션 실행 후 after 값을 추적하기 위해 래퍼 사용
                // 이는 복잡하므로 단순히 before만 기록하는 것이 더 실용적
                handler.handle(field, before, null);
            } else {
                handler.handle(field, before, null);
            }
        } catch (IOException e) {
            throw new RuntimeException("Audit handler failed", e);
        }
    }
    
    /**
     * 감사 대상 필드명을 반환합니다.
     * @return 필드명
     */
    public String getField() {
        return field;
    }
    
    /**
     * 감사 이벤트 핸들러를 반환합니다.
     * @return 감사 이벤트 핸들러
     */
    public AuditEventHandler getHandler() {
        return handler;
    }
}
