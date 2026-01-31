package com.he180773.testreact.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class JsonMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> jsonToList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // Log nếu cần
            return Collections.emptyList(); // hoặc throw nếu bạn muốn bắt lỗi ở chỗ gọi
        }
    }

    public static String listToJson(List<String> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            // Log nếu cần
            return "[]"; // hoặc throw
        }
    }
}
