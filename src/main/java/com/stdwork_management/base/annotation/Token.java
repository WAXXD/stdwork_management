package com.stdwork_management.base.annotation;

import java.lang.annotation.*;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-29
 **/
@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

    boolean requireAuthorize() default true;

    String accountType() default "std";
}
