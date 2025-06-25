package com.masking.pipeline;

import com.masking.step.Step;

import java.util.List;
import java.util.Map;

public class MaskPipeline {
    private final List<Step> steps;

    MaskPipeline(List<Step> steps) {
        this.steps = steps;
    }

    public static MaskPipelineBuilder builder() {
        return new MaskPipelineBuilder();
    }

    /** 옵션: YAML 설정 로더 (추후 구현) */
    // public static MaskPipeline fromYaml(String path) { … }

    /**
     * 파이프라인 실행
     */
    public Map<String, Object> apply(Map<String, Object> record) {
        for (Step step : steps) {
            step.process(record);
        }
        return record;
    }
}
