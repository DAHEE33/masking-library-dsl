package com.masking.step;

import com.masking.strategy.UUIDTokenizationStrategy;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenizeFieldStepTest {

    @Test
    void uuidTokenizeReplacesValue() {
        // given
        Step step = TokenizeFieldStep.of("userId", UUIDTokenizationStrategy.create());
        Map<String,Object> record = new HashMap<>();
        record.put("userId", "originalValue");

        // when
        step.process(record);

        // then
        Object token = record.get("userId");
        assertNotNull(token);
        assertTrue(token instanceof String);
        assertNotEquals("originalValue", token);
        // 간단히 UUID 형식 체크
        assertDoesNotThrow(() -> UUID.fromString(token.toString()));

        System.out.println("토큰화 결과: " + record.get("userId"));
    }
}
