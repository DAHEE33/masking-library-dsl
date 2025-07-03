package com.masking.action;

import java.io.IOException;
import java.util.Map;

public interface Action {
    void apply(Map<String, String> record) throws IOException;
}
