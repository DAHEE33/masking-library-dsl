package com.masking.action;

import com.masking.audit.AuditEventHandler;
import com.masking.strategy.mask.RegexMaskStrategy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompositeAuditActionTest {
    
    static class TestHandler implements AuditEventHandler {
        List<String> calls = new ArrayList<>();
        
        @Override
        public void handle(String field, String before, String after) {
            calls.add(field + ":" + before + "->" + after);
        }
    }

    @Test
    void compositeAuditAction_shouldTrackBeforeAndAfter() throws IOException {
        TestHandler handler = new TestHandler();
        MaskAction maskAction = MaskAction.of("email", 
            RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'));
        
        CompositeAuditAction action = CompositeAuditAction.of("email", handler, maskAction);

        Map<String, String> record = new HashMap<>();
        record.put("email", "user@example.com");

        action.apply(record);

        assertEquals(1, handler.calls.size());
        assertEquals("email:user@example.com->u**r@example.com", handler.calls.get(0));
        assertEquals("u**r@example.com", record.get("email"));
    }

    @Test
    void compositeAuditAction_withNullValue_shouldHandleGracefully() throws IOException {
        TestHandler handler = new TestHandler();
        MaskAction maskAction = MaskAction.of("email", 
            RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'));
        
        CompositeAuditAction action = CompositeAuditAction.of("email", handler, maskAction);

        Map<String, String> record = new HashMap<>();
        record.put("email", null);

        action.apply(record);

        assertEquals(1, handler.calls.size());
        assertEquals("email:null->null", handler.calls.get(0));
        assertNull(record.get("email"));
    }
} 