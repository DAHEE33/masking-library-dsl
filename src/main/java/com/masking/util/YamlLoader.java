package com.masking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class YamlLoader {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    // 기존 File 기반 메서드
    public static <T> T load(File yamlFile, Class<T> clazz) {
        try {
            return mapper.readValue(yamlFile, clazz);
        } catch (IOException e) {
            throw new RuntimeException("YAML 로딩 실패: " + yamlFile, e);
        }
    }

    // ▶ 추가: InputStream 기반 오버로드
    public static <T> T load(InputStream is, Class<T> clazz) {
        try {
            return mapper.readValue(is, clazz);
        } catch (IOException e) {
            throw new RuntimeException("YAML 로딩 실패 (InputStream)", e);
        }
    }
}
