package com.masking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class YamlLoader {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * YAML 파일을 읽어, 지정한 설정 클래스 타입으로 매핑해 반환
     */
    public static <T> T load(File yamlFile, Class<T> clazz) {
        try {
            return mapper.readValue(yamlFile, clazz);
        } catch (IOException e) {
            throw new RuntimeException("YAML 로딩 실패: " + yamlFile, e);
        }
    }
}
