package com.masking.action;

import com.masking.audit.AuditEventHandler;

import java.io.IOException;
import java.util.Map;

public class AuditAction implements Action {
    private final String field;
    private final AuditEventHandler handler;

    private AuditAction(String field, AuditEventHandler handler) {
        this.field = field;
        this.handler = handler;
    }

    public static AuditAction of(String field, AuditEventHandler handler) {
        return new AuditAction(field, handler);
    }

    @Override
    public void apply(Map<String, String> record) throws IOException {
        String before = record.get(field);
        // 다음 액션이 바뀐 값을 넣어줄 수 있도록, 미리 이벤트 호출
        handler.handle(field, before, null);
        // (여기서는 실제 수정 액션이 아니라, Audit만 담당하므로 기록용)
        // 만약 before/after를 모두 보고 싶으면, 실제 변경 액션과 조합해서 사용하세요.
    }
}
