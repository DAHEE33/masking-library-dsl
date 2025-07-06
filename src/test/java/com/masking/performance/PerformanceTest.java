package com.masking.performance;

import com.masking.action.Action;
import com.masking.action.MaskAction;
import com.masking.action.TokenizeAction;
import com.masking.action.EncryptAction;
import com.masking.action.Actions;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;
import com.masking.strategy.encrypt.AesEncryptionStrategy;
import com.masking.pipeline.MaskPipelineBuilder;
import com.masking.pipeline.MaskPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 라이브러리 성능 테스트 및 벤치마크
 * 
 * 다양한 시나리오에서 라이브러리의 성능을 측정합니다.
 */
public class PerformanceTest {
    
    private List<Map<String, String>> testData;
    private static final int DATA_SIZE = 10000;
    
    @BeforeEach
    void setUp() {
        testData = generateTestData(DATA_SIZE);
    }
    
    /**
     * 단일 Action 성능 테스트
     */
    @Test
    void testSingleActionPerformance() {
        Action maskAction = MaskAction.of("email", 
            RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'));
        
        long startTime = System.nanoTime();
        
        for (Map<String, String> record : testData) {
            maskAction.apply(record);
        }
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        System.out.printf("단일 Action 성능: %d 레코드 처리에 %dms 소요 (%.2f 레코드/초)%n", 
            DATA_SIZE, duration, (double) DATA_SIZE / duration * 1000);
        
        assertTrue(duration < 5000, "성능이 너무 느림: " + duration + "ms");
    }
    
    /**
     * 복합 Action 성능 테스트
     */
    @Test
    void testCompositeActionPerformance() {
        Actions actions = Actions.of(
            MaskAction.of("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')),
            TokenizeAction.of("username", UUIDTokenizationStrategy.of()),
            EncryptAction.of("ssn", AesEncryptionStrategy.of(new byte[32]))
        );
        
        long startTime = System.nanoTime();
        
        for (Map<String, String> record : testData) {
            actions.apply(record);
        }
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        System.out.printf("복합 Action 성능: %d 레코드 처리에 %dms 소요 (%.2f 레코드/초)%n", 
            DATA_SIZE, duration, (double) DATA_SIZE / duration * 1000);
        
        assertTrue(duration < 10000, "성능이 너무 느림: " + duration + "ms");
    }
    
    /**
     * 파이프라인 성능 테스트
     */
    @Test
    void testPipelinePerformance() {
        MaskPipelineBuilder builder = MaskPipelineBuilder.newBuilder()
            .mask("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))
            .tokenize("username", UUIDTokenizationStrategy.of())
            .encryptAes("ssn", new byte[32]);
        MaskPipeline pipeline = builder.build();
        
        long startTime = System.nanoTime();
        
        for (Map<String, String> record : testData) {
            pipeline.apply(record);
        }
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        System.out.printf("파이프라인 성능: %d 레코드 처리에 %dms 소요 (%.2f 레코드/초)%n", 
            DATA_SIZE, duration, (double) DATA_SIZE / duration * 1000);
        
        assertTrue(duration < 10000, "성능이 너무 느림: " + duration + "ms");
    }
    
    /**
     * 메모리 사용량 테스트
     */
    @Test
    void testMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        
        // GC 실행
        System.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 대량 데이터 처리
        List<Map<String, String>> largeData = generateTestData(100000);
        Action maskAction = MaskAction.of("email", 
            RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'));
        
        for (Map<String, String> record : largeData) {
            maskAction.apply(record);
        }
        
        // GC 실행 후 메모리 측정
        System.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        System.out.printf("메모리 사용량: %d MB%n", memoryUsed / (1024 * 1024));
        
        // 메모리 누수 확인 (100MB 이하)
        assertTrue(memoryUsed < 100 * 1024 * 1024, 
            "메모리 사용량이 너무 큼: " + memoryUsed / (1024 * 1024) + "MB");
    }
    
    /**
     * 동시성 테스트
     */
    @Test
    void testConcurrency() throws InterruptedException {
        Action maskAction = MaskAction.of("email", 
            RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'));
        
        int threadCount = 10;
        int recordsPerThread = DATA_SIZE / threadCount;
        
        Thread[] threads = new Thread[threadCount];
        long startTime = System.nanoTime();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                List<Map<String, String>> threadData = 
                    testData.subList(threadIndex * recordsPerThread, 
                                   (threadIndex + 1) * recordsPerThread);
                
                for (Map<String, String> record : threadData) {
                    maskAction.apply(record);
                }
            });
            threads[i].start();
        }
        
        // 모든 스레드 완료 대기
        for (Thread thread : threads) {
            thread.join();
        }
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        System.out.printf("동시성 테스트: %d 스레드로 %d 레코드 처리에 %dms 소요%n", 
            threadCount, DATA_SIZE, duration);
        
        assertTrue(duration < 10000, "동시성 성능이 너무 느림: " + duration + "ms");
    }
    
    /**
     * 테스트 데이터 생성
     */
    private List<Map<String, String>> generateTestData(int size) {
        List<Map<String, String>> data = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            Map<String, String> record = new HashMap<>();
            record.put("email", "user" + i + "@example.com");
            record.put("username", "user" + i);
            record.put("ssn", "123-45-" + String.format("%04d", i));
            record.put("phone", "010-1234-" + String.format("%04d", i));
            data.add(record);
        }
        
        return data;
    }
} 