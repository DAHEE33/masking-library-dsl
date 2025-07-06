package com.masking.action;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;

import java.util.Map;

/**
 * 데이터 보호 작업을 수행하는 Action 인터페이스
 */
public interface Action {
    
    /**
     * 레코드에 Action을 적용합니다.
     * @param record 처리할 레코드
     */
    void apply(Map<String, String> record);
    
    /**
     * 메트릭을 포함하여 Action을 적용합니다.
     * @param record 처리할 레코드
     * @param meterRegistry 메트릭 레지스트리
     */
    default void applyWithMetrics(Map<String, String> record, MeterRegistry meterRegistry) {
        Timer.Sample sample = Timer.start(meterRegistry);
        Counter counter = Counter.builder("masking.action.count")
            .tag("action", this.getClass().getSimpleName())
            .register(meterRegistry);
        
        try {
            apply(record);
            counter.increment();
        } finally {
            sample.stop(Timer.builder("masking.action.duration")
                .tag("action", this.getClass().getSimpleName())
                .register(meterRegistry));
        }
    }
}
