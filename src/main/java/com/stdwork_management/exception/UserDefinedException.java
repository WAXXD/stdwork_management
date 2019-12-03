package com.stdwork_management.exception;

import com.stdwork_management.base.Result;

import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-28
 **/
public class UserDefinedException extends RuntimeException {

    private static final long serialVersionUID = 2357731261050504950L;
    Result result = new Result();

    public UserDefinedException(Integer code, String message) {
        super(message);
        result.setCode(code);
        result.setMessage(message);
    }

    public UserDefinedException(Integer code,String message, List<String> messages){
        super(message);
        result.setCode(code);
        result.setMessage(message);
        result.setMessages(messages);

    }



    public Result getResult() {
        return result;
    }
}
