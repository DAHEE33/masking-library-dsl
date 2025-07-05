package com.masking.config;

import java.util.HashSet;
import java.util.Set;

/**
 * 감사 알림 설정을 관리하는 클래스
 * 
 * 사용자가 원하는 감사 채널만 선택적으로 활성화/비활성화할 수 있습니다.
 */
public class AuditConfig {
    
    public enum AuditChannel {
        SLACK, EMAIL, DATABASE, CONSOLE
    }
    
    private static final Set<AuditChannel> enabledChannels = new HashSet<>();
    
    static {
        // 기본값: 모든 채널 활성화
        enableAllChannels();
    }
    
    /**
     * 모든 감사 채널을 활성화합니다.
     */
    public static void enableAllChannels() {
        enabledChannels.clear();
        for (AuditChannel channel : AuditChannel.values()) {
            enabledChannels.add(channel);
        }
    }
    
    /**
     * 모든 감사 채널을 비활성화합니다.
     */
    public static void disableAllChannels() {
        enabledChannels.clear();
    }
    
    /**
     * 특정 감사 채널을 활성화합니다.
     * 
     * @param channel 활성화할 채널
     */
    public static void enableChannel(AuditChannel channel) {
        enabledChannels.add(channel);
    }
    
    /**
     * 특정 감사 채널을 비활성화합니다.
     * 
     * @param channel 비활성화할 채널
     */
    public static void disableChannel(AuditChannel channel) {
        enabledChannels.remove(channel);
    }
    
    /**
     * 특정 감사 채널이 활성화되어 있는지 확인합니다.
     * 
     * @param channel 확인할 채널
     * @return 활성화 여부
     */
    public static boolean isChannelEnabled(AuditChannel channel) {
        return enabledChannels.contains(channel);
    }
    
    /**
     * 활성화된 모든 채널을 반환합니다.
     * 
     * @return 활성화된 채널들의 Set
     */
    public static Set<AuditChannel> getEnabledChannels() {
        return new HashSet<>(enabledChannels);
    }
    
    /**
     * 환경변수나 시스템 프로퍼티를 통해 채널을 설정합니다.
     * 
     * @param channelsConfig 콤마로 구분된 채널명 (예: "SLACK,EMAIL")
     */
    public static void configureFromString(String channelsConfig) {
        if (channelsConfig == null || channelsConfig.trim().isEmpty()) {
            return;
        }
        
        enabledChannels.clear();
        String[] channels = channelsConfig.toUpperCase().split(",");
        
        for (String channel : channels) {
            try {
                AuditChannel auditChannel = AuditChannel.valueOf(channel.trim());
                enabledChannels.add(auditChannel);
            } catch (IllegalArgumentException e) {
                System.err.println("알 수 없는 감사 채널: " + channel);
            }
        }
    }
} 