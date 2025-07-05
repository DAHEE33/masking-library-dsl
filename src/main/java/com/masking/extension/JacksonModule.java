package com.masking.extension;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.masking.action.Action;
import com.masking.action.MaskAction;
import com.masking.action.TokenizeAction;
import com.masking.action.EncryptAction;
import com.masking.action.AuditAction;
import com.masking.strategy.mask.MaskStrategy;
import com.masking.strategy.tokenize.TokenizationStrategy;
import com.masking.strategy.encrypt.EncryptionStrategy;

/**
 * Jackson 모듈을 통한 Action/Step 확장 지원
 * 
 * Action과 Strategy들을 JSON으로 직렬화/역직렬화할 수 있도록 합니다.
 * 이를 통해 설정 파일이나 외부 시스템과의 연동이 가능합니다.
 */
public class JacksonModule extends SimpleModule {
    
    private static final long serialVersionUID = 1L;
    
    public JacksonModule() {
        super("MaskingLibraryModule");
        
        // Action 직렬화/역직렬화 등록
        addSerializer(Action.class, new ActionSerializer());
        addDeserializer(Action.class, new ActionDeserializer());
        
        // Strategy 직렬화/역직렬화 등록 (임시로 비활성화)
        // addSerializer(MaskStrategy.class, new StrategySerializer());
        // addDeserializer(MaskStrategy.class, new StrategyDeserializer());
        
        // addSerializer(TokenizationStrategy.class, new StrategySerializer());
        // addDeserializer(TokenizationStrategy.class, new StrategyDeserializer());
        
        // addSerializer(EncryptionStrategy.class, new StrategySerializer());
        // addDeserializer(EncryptionStrategy.class, new StrategyDeserializer());
    }
    
    /**
     * 기본 설정이 적용된 ObjectMapper를 생성합니다.
     * 
     * @return 설정된 ObjectMapper
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JacksonModule());
        
        // JSON 출력을 보기 좋게 포맷팅
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // 알 수 없는 프로퍼티는 무시
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        return mapper;
    }
} 