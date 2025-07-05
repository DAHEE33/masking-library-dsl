package com.masking.extension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Strategy를 JSON으로 직렬화하는 Serializer
 */
public class StrategySerializer extends JsonSerializer<Object> {
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public void serialize(Object strategy, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        
        ObjectNode node = mapper.createObjectNode();
        
        // Strategy 타입 정보 추가
        node.put("type", strategy.getClass().getSimpleName());
        
        gen.writeTree(node);
    }
} 