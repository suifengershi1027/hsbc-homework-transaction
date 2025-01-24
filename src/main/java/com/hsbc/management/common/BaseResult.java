package com.hsbc.management.common;

import lombok.Data;

@Data
public class BaseResult<T> {
    private int code;

    private String msg;

    private T data;

    public BaseResult(ResultCodeEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public BaseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static BaseResult succeed() {
        return new BaseResult(ResultCodeEnum.SUCCESS);
    }

    public static <T> BaseResult<T> succeed(T data) {
        BaseResult baseResult = new BaseResult(ResultCodeEnum.SUCCESS);
        baseResult.setData(data);
        return baseResult;
    }

    public static <T> BaseResult<T> fail(ResultCodeEnum resultCodeEnum) {
        return new BaseResult(resultCodeEnum);
    }

    public static <T> BaseResult<T> fail(int code, String msg) {
        return new BaseResult(code, msg);
    }

}
