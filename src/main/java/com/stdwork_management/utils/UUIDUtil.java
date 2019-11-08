package com.stdwork_management.utils;

import java.util.UUID;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
public class UUIDUtil {

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
