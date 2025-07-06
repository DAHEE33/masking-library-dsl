package com.masking.action;

import com.masking.audit.AuditEventHandler;

import java.io.IOException;
import java.util.Map;

/**
 * 액션 실행 전후 값을 모두 추적하는 복합 감사 액션
 * 사용법: CompositeAuditAction.of("email", handler, maskAction)
 */
public class CompositeAuditAction implements Action {
    private final String field;
    private final AuditEventHandler handler;
    private final Action targetAction;

    private CompositeAuditAction(String field, AuditEventHandler handler, Action targetAction) {
        this.field = field;
        this.handler = handler;
        this.targetAction = targetAction;
    }

    public static CompositeAuditAction of(String field, AuditEventHandler handler, Action targetAction) {
        return new CompositeAuditAction(field, handler, targetAction);
    }

    @Override
    public void apply(Map<String, String> record) {
        String before = record.get(field);
        
        // 타겟 액션 실행
        targetAction.apply(record);
        
        // 실행 후 값 추출
        String after = record.get(field);
        
        // 감사 로그 기록
        try {
            handler.handle(field, before, after);
        } catch (IOException e) {
            throw new RuntimeException("Audit handler failed", e);
        }
    }
} 