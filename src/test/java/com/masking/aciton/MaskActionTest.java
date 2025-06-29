package com.masking.aciton;

import com.masking.action.MaskAction;
import com.masking.strategy.PartialMaskStrategy;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MaskActionTest {

    @Test
    void partialMask_shouldMaskCorrectly() {
        // 준비
        MaskAction action = MaskAction.of("username", PartialMaskStrategy.of(2, 2, '*'));
        Map<String, String> record = new HashMap<>();
        record.put("username", "maskingUser");

        // 실행
        action.apply(record);

        // 검증
        assertEquals("ma*******er", record.get("username"));
    }

    //입력값이 null임에도 그대로 진핼(에러 없이 null 확인)
    @Test
    void nullValue_shouldRemainNull() {
        MaskAction action = MaskAction.of("email", PartialMaskStrategy.of(1, 1, '*'));
        Map<String, String> record = new HashMap<>();
        record.put("email", null);

        action.apply(record);
        assertNull(record.get("email"));
    }
}
