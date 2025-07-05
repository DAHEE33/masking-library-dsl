package com.masking.extension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masking.action.Action;
import com.masking.action.MaskAction;
import com.masking.action.TokenizeAction;
import com.masking.action.EncryptAction;
import com.masking.action.AuditAction;

import java.io.IOException;

/**
 * Action을 JSON으로 직렬화하는 Serializer
 */
public class ActionSerializer extends JsonSerializer<Action> {
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public void serialize(Action action, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        
        ObjectNode node = mapper.createObjectNode();
        
        // Action 타입 정보 추가
        node.put("type", action.getClass().getSimpleName());
        
        // Action별 특정 정보 추가
        if (action instanceof MaskAction) {
            MaskAction maskAction = (MaskAction) action;
            node.put("field", maskAction.getField());
            node.put("strategy", maskAction.getStrategy().getClass().getSimpleName());
            
        } else if (action instanceof TokenizeAction) {
            TokenizeAction tokenizeAction = (TokenizeAction) action;
            node.put("field", tokenizeAction.getField());
            node.put("strategy", tokenizeAction.getStrategy().getClass().getSimpleName());
            
        } else if (action instanceof EncryptAction) {
            EncryptAction encryptAction = (EncryptAction) action;
            node.put("field", encryptAction.getField());
            node.put("strategy", encryptAction.getStrategy().getClass().getSimpleName());
            
        } else if (action instanceof AuditAction) {
            AuditAction auditAction = (AuditAction) action;
            node.put("field", auditAction.getField());
            node.put("handler", auditAction.getHandler().getClass().getSimpleName());
        }
        
        gen.writeTree(node);
    }
} 