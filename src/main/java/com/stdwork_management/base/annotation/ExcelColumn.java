package com.stdwork_management.base.annotation;

import java.lang.annotation.*;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-29
 **/

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ExcelColumn {

    String value() default "";

    int column() default 0;
}
