package com.masking.aciton;

import com.masking.action.TokenizeAction;
import com.masking.strategy.UUIDTokenizationStrategy;
import com.masking.strategy.HashTokenizationStrategy;
import com.masking.strategy.NumericTokenizationStrategy;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TokenizeActionTest {

    @Test
    void uuidTokenize_shouldGenerateUuid() {
        TokenizeAction action = TokenizeAction.of("id", UUIDTokenizationStrategy.of());
        Map<String, String> record = new HashMap<>();
        record.put("id", "anyValue");

        action.apply(record);
        assertNotNull(record.get("id"));
        assertDoesNotThrow(() -> UUID.fromString(record.get("id")));
    }

    @Test
    void hashTokenize_shouldHashWithSalt() {
        String salt = "salt";
        String input = "value";
        TokenizeAction action = TokenizeAction.of("field", HashTokenizationStrategy.of(salt));
        Map<String, String> record = new HashMap<>();
        record.put("field", input);

        action.apply(record);
        String hashed = record.get("field");
        assertNotNull(hashed);
        assertEquals(64, hashed.length());
    }

    @Test
    void numericTokenize_shouldGenerateFixedLengthNumber() {
        TokenizeAction action = TokenizeAction.of("num", NumericTokenizationStrategy.of(6));
        Map<String, String> record = new HashMap<>();
        record.put("num", "any");

        action.apply(record);
        String token = record.get("num");
        assertNotNull(token);
        assertEquals(6, token.length());
        assertTrue(token.matches("\\d{6}"));
    }
}
