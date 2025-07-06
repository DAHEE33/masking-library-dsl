package com.masking.pipeline;

import com.masking.action.Action;

import java.io.IOException;
import java.util.Map;

public class MaskPipeline implements Action {
    private final Action pipeline;

    /**
     * MaskPipeline 인스턴스를 생성합니다.
     * @param pipeline 파이프라인으로 실행할 Action
     */
    private MaskPipeline(Action pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * MaskPipeline 인스턴스를 생성합니다.
     * @param pipeline 파이프라인으로 실행할 Action
     * @return MaskPipeline 인스턴스
     */
    public static MaskPipeline of(Action pipeline) {
        return new MaskPipeline(pipeline);
    }

    /**
     * 파이프라인을 실행합니다.
     * @param record 처리할 레코드
     */
    @Override
    public void apply(Map<String, String> record) {
        pipeline.apply(record);
    }
}

