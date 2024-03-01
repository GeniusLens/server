package xyz.thuray.geniuslens.server.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用返回结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private String code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return new Result<>("200", "成功！", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>("200", "成功！", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>("200", msg, data);
    }

    public static <T> Result<T> success(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public static <T> Result<T> fail(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public static Result<?> fail(String msg) {
        return new Result<>("500", msg, null);
    }

    public static Result<?> fail() {
        return new Result<>("500", "垃！", null);
    }
}
