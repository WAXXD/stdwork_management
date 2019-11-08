package com.stdwork_management.utils;

import com.stdwork_management.exception.UserDefinedException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
public class BindingResultUtil {

    public static void checkInputParams(BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<String> messages = new ArrayList<>();
            for (int i = 0; i < bindingResult.getFieldErrors().size(); i++){
                messages.add(bindingResult.getFieldErrors().get(i).getField() + "--->" + bindingResult.getFieldErrors().get(i).getDefaultMessage());
            }
            throw new UserDefinedException(9999, "输入参数有误", messages);
        }
    }

}
