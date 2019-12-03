package com.stdwork_management.exception;

import com.stdwork_management.base.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-28
 **/
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserDefinedException.class)
    public Result userDefinedException(UserDefinedException udex){
        return udex.getResult();
    }

    @ExceptionHandler(Exception.class)
    public Result exception(Exception ex) {
        ex.printStackTrace();
        return new Result().setCode(9999).setMessage(ex.getCause().getMessage());
    }
}
