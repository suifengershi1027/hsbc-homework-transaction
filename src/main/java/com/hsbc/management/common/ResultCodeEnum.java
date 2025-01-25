package com.hsbc.management.common;

public enum ResultCodeEnum {

    SUCCESS(0, "成功"),
    PARAM_ERROR(1001, "参数校验错误"),
    BIZ_ERROR(1002, "业务错误"),
    SYSTEM_ERROR(-1, "系统错误");

    private int code;
    private String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
