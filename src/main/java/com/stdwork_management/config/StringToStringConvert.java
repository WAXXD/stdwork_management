package com.stdwork_management.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-12
 **/
public class StringToStringConvert implements Converter<String, String> {
    @Override
    public String convert(String source) {
        if(StringUtils.isEmpty(source)){
            return null;
        } else {
            return source;
        }
    }
}
