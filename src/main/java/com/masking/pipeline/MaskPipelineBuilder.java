package com.masking.pipeline;

import com.masking.step.Step;

import java.util.ArrayList;
import java.util.List;

public class MaskPipelineBuilder {
    private final List<Step> steps = new ArrayList<>();

    public MaskPipelineBuilder step(Step step) {
        steps.add(step);
        return this;
    }

    public MaskPipeline build() {
        return new MaskPipeline(List.copyOf(steps));
    }
}
