package com.stdwork_management.base;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-09-19
 **/
public enum  ResultCodeType {

    SUCCESS(0, "成功");

    private Integer code;
    private String message;

    ResultCodeType(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
