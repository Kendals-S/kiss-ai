package com.ks.kissai.pojo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record R<T>(@JsonPropertyDescription("状态码") Integer code,
                   @JsonPropertyDescription ("状态信息") String message,
                   @JsonPropertyDescription("数据") T data) {

    public static <T> R<T> success() {
        return new R<>(200, "success", null);
    }

    public static <T> R<T> success(T data) {
        return new R<>(200, "success", data);
    }

    public static <T> R<T> error(Integer code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> error(String message) {
        return new R<>(500, message, null);
    }
}
