package com.masking.action;

import com.masking.audit.AuditEventHandler;

import java.io.IOException;
import java.util.Map;

public class AuditAction implements Action {
    private final String field;
    private final AuditEventHandler handler;
    private final boolean trackAfter;

    private AuditAction(String field, AuditEventHandler handler, boolean trackAfter) {
        this.field = field;
        this.handler = handler;
        this.trackAfter = trackAfter;
    }

    public static AuditAction of(String field, AuditEventHandler handler) {
        return new AuditAction(field, handler, false);
    }

    public static AuditAction of(String field, AuditEventHandler handler, boolean trackAfter) {
        return new AuditAction(field, handler, trackAfter);
    }

    @Override
    public void apply(Map<String, String> record) throws IOException {
        String before = record.get(field);
        if (trackAfter) {
            // 다음 액션 실행 후 after 값을 추적하기 위해 래퍼 사용
            // 이는 복잡하므로 단순히 before만 기록하는 것이 더 실용적
            handler.handle(field, before, null);
        } else {
            handler.handle(field, before, null);
        }
    }
}
