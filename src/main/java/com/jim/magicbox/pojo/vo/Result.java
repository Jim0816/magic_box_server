package com.jim.magicbox.pojo.vo;
import com.jim.magicbox.common.enums.ResCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ResData
 * @Description 返回前端对象
 * @Author Jim
 * @Date 2022/2/22 16:17
 **/
@Data
public class Result<T> implements Serializable {
    private long code;
    private T data;
    private String msg;

    public Result() {
    }

    public static Result ok() {
        return restResult(null, ResCodeEnum.SUCCESS.getCode(), ResCodeEnum.SUCCESS.getMsg());
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, ResCodeEnum.SUCCESS.getCode(), ResCodeEnum.SUCCESS.getMsg());
    }

    public static <T> Result<T> ok(long code, String msg, T data) {
        return restResult(data, code, msg);
    }

    public static <T> Result<T> ok(T data, ResCodeEnum resCodeEnum) {
        return (Result<T>) restResult(data, resCodeEnum.getCode(), resCodeEnum.getMsg());
    }

    public static Result failed() {
        return restResult(null, ResCodeEnum.FAILED.getCode(), ResCodeEnum.FAILED.getMsg());
    }

    public static Result failed(String msg) {
        return restResult(null, ResCodeEnum.FAILED.getCode(), msg);
    }

    public static <T> Result<T> failed(long code, String msg, T data) {
        return restResult(data, code, msg);
    }

    public static <T> Result<T> failed(ResCodeEnum resCodeEnum) {
        return (Result<T>) restResult((Object)null, resCodeEnum.getCode(), resCodeEnum.getMsg());
    }

    private static <T> Result<T> restResult(T data, long code, String msg) {
        Result<T> result = new Result();
        result.setCode(code);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }
}