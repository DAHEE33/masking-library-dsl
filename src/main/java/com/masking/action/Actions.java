package com.masking.action;

import java.util.*;
import java.util.Map;

/**
 * 	여러 Action을 순차 합성·실행
 */
public class Actions implements Action {
    private final List<Action> actions;

    private Actions(List<Action> actions) {
        this.actions = actions;
    }

    public static Actions of(Action... actions) {
        return new Actions(Arrays.asList(actions));
    }

    @Override
    public void apply(Map<String, String> record) {
        actions.forEach(a -> a.apply(record));
    }
}
