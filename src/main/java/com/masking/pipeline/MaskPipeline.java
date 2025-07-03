package com.masking.pipeline;

import com.masking.action.Action;

import java.io.IOException;
import java.util.Map;

public class MaskPipeline implements Action {
    private final Action pipeline;

    MaskPipeline(Action pipeline) {
        this.pipeline = pipeline;
    }

    public static MaskPipeline of(Action pipeline) {
        return new MaskPipeline(pipeline);
    }

    @Override
    public void apply(Map<String, String> record) throws IOException {
        pipeline.apply(record);
    }
}

