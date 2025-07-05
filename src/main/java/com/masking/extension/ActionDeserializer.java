package com.masking.extension;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.masking.action.Action;
import com.masking.spi.ActionRegistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Action을 JSON에서 역직렬화하는 Deserializer
 */
public class ActionDeserializer extends JsonDeserializer<Action> {
    
    @Override
    public Action deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        String type = node.get("type").asText();
        Map<String, Object> config = new HashMap<>();
        
        // JSON 노드의 모든 필드를 config에 추가
        node.fields().forEachRemaining(entry -> {
            if (!"type".equals(entry.getKey())) {
                config.put(entry.getKey(), entry.getValue().asText());
            }
        });
        
        // ActionRegistry를 통해 Action 생성
        return ActionRegistry.createAction(type, config);
    }
} 