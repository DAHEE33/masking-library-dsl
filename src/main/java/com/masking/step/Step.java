package com.masking.step;

import java.util.Map;

public interface Step {
    /**
     * 레코드의 특정 필드를 읽어서 처리한 뒤, 수정된 레코드를 반환
     * @param record 처리 대상 레코드(Map 형태)
     */
    void process(Map<String, Object> record);
}
