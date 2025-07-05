package com.masking.extension;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Strategy를 JSON에서 역직렬화하는 Deserializer
 * 
 * 현재는 기본 구현만 제공합니다.
 * 실제 사용 시에는 각 Strategy 타입별로 구체적인 역직렬화 로직이 필요합니다.
 */
public class StrategyDeserializer extends JsonDeserializer<Object> {
    
    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        // 현재는 기본 구현만 제공
        // 실제 사용 시에는 Strategy 타입별로 구체적인 역직렬화 로직 구현 필요
        throw new UnsupportedOperationException("Strategy 역직렬화는 아직 구현되지 않았습니다.");
    }
} 