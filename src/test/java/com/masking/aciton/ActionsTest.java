package com.masking.aciton;

import com.masking.action.Actions;
import com.masking.action.MaskAction;
import com.masking.strategy.mask.PartialMaskStrategy;
import com.masking.strategy.mask.RegexMaskStrategy;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ActionsTest {

    @Test
    void multipleActions_shouldApplyAllInOrder() {
        MaskAction maskEmail = MaskAction.of("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'));
        MaskAction maskUser  = MaskAction.of("username", PartialMaskStrategy.of(2, 2, '*'));
        Actions pipeline     = Actions.of(maskEmail, maskUser);

        Map<String, String> record = new HashMap<>();
        record.put("email",    "dahee@example.com");
        record.put("username", "maskingUser");

        pipeline.apply(record);

        assertEquals("d***e@example.com", record.get("email"));
        assertEquals("ma*******er",              record.get("username"));
    }
}
