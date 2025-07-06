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

    /**
     * 여러 Action을 합성하여 Actions 인스턴스를 생성합니다.
     * @param actions 합성할 Action 목록
     * @return Actions 인스턴스
     */
    public static Actions of(Action... actions) {
        return new Actions(Arrays.asList(actions));
    }

    /**
     * 모든 Action을 순차적으로 적용합니다.
     * @param record 처리할 레코드
     */
    @Override
    public void apply(Map<String, String> record) {
        actions.forEach(a -> a.apply(record));
    }
}
