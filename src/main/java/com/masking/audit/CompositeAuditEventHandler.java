package com.masking.audit;

import com.masking.config.AuditConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 여러 감사 핸들러를 조합하여 관리하는 복합 핸들러
 * 
 * AuditConfig를 통해 활성화된 채널만 선택적으로 감사 이벤트를 처리합니다.
 */
public class CompositeAuditEventHandler implements AuditEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(CompositeAuditEventHandler.class);
    
    private final List<AuditEventHandler> handlers = new ArrayList<>();
    
    /**
     * 기본 핸들러들을 추가하여 복합 핸들러를 생성합니다.
     */
    public CompositeAuditEventHandler() {
        // 설정에 따라 핸들러 추가
        if (AuditConfig.isChannelEnabled(AuditConfig.AuditChannel.CONSOLE)) {
            handlers.add(new ConsoleAuditEventHandler());
        }
        
        if (AuditConfig.isChannelEnabled(AuditConfig.AuditChannel.SLACK)) {
            try {
                handlers.add(new SlackAuditEventHandler());
            } catch (Exception e) {
                logger.warn("Slack 핸들러 초기화 실패: {}", e.getMessage());
            }
        }
        
        if (AuditConfig.isChannelEnabled(AuditConfig.AuditChannel.EMAIL)) {
            try {
                handlers.add(new EmailAuditEventHandler());
            } catch (Exception e) {
                logger.warn("Email 핸들러 초기화 실패: {}", e.getMessage());
            }
        }
        
        if (AuditConfig.isChannelEnabled(AuditConfig.AuditChannel.DATABASE)) {
            try {
                // Database 핸들러는 DataSource가 필요하므로 나중에 별도로 추가
                logger.info("Database 핸들러는 DataSource 설정 후 별도로 추가해야 합니다.");
            } catch (Exception e) {
                logger.warn("Database 핸들러 초기화 실패: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 사용자 정의 핸들러들을 추가하여 복합 핸들러를 생성합니다.
     * 
     * @param customHandlers 사용자 정의 핸들러들
     */
    public CompositeAuditEventHandler(AuditEventHandler... customHandlers) {
        for (AuditEventHandler handler : customHandlers) {
            if (handler != null) {
                handlers.add(handler);
            }
        }
    }
    
    /**
     * 감사 이벤트를 모든 활성화된 핸들러에 전달합니다.
     * 
     * @param field 변경된 필드명
     * @param before 변경 전 값
     * @param after 변경 후 값
     */
    @Override
    public void handle(String field, String before, String after) {
        if (handlers.isEmpty()) {
            logger.warn("활성화된 감사 핸들러가 없습니다.");
            return;
        }
        
        for (AuditEventHandler handler : handlers) {
            try {
                handler.handle(field, before, after);
            } catch (Exception e) {
                logger.error("감사 핸들러 실행 중 오류 발생: {} - {}", 
                    handler.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 등록된 핸들러 수를 반환합니다.
     * 
     * @return 핸들러 수
     */
    public int getHandlerCount() {
        return handlers.size();
    }
    
    /**
     * 등록된 핸들러 목록을 반환합니다.
     * 
     * @return 핸들러 목록
     */
    public List<AuditEventHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }
} 