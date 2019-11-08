package com.stdwork_management.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-09-19
 **/
@Data
@Accessors(chain = true)
public class Result<T> {

    private Integer code;

    private String message;

    private Integer count;

    private List<String> messages;

    private Long currentTimeMills = System.currentTimeMillis();

    private T data;

    public Result () {
        this(ResultCodeType.SUCCESS);
    }

    public Result (ResultCodeType type){
        this.code = type.getCode();
        this.message = type.getMessage();
    }

}
