package com.masking.step;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

class MaskFieldStepTest {

    @Test
    void maskEmail() {
        // given
        var step = MaskFieldStep.of("email", 2, 2, '*');
        Map<String, Object> record = new HashMap<>();
        record.put("email", "abcdefg");

        // when
        step.process(record);

        // then
        assertEquals("ab***fg", record.get("email"));

        System.out.println("마스킹 결과: " + record.get("email"));
    }
}
