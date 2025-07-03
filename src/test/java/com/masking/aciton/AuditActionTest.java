package com.masking.aciton;

import com.masking.action.AuditAction;
import com.masking.audit.AuditEventHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AuditActionTest {
    static class TestHandler implements AuditEventHandler {
        List<String> calls = new ArrayList<>();
        @Override
        public void handle(String field, String before, String after) {
            calls.add(field + ":" + before + "->" + after);
        }
    }

    @Test
    void auditAction_shouldInvokeHandlerWithBeforeAndNullAfter() throws IOException {
        TestHandler handler = new TestHandler();
        AuditAction action = AuditAction.of("email", handler);

        Map<String,String> record = new HashMap<>();
        record.put("email", "user@example.com");

        action.apply(record);

        assertEquals(1, handler.calls.size());
        assertEquals("email:user@example.com->null", handler.calls.get(0));
    }
}
