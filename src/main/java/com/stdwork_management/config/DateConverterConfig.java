package com.stdwork_management.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * description:
 *          时间转换器
 * @author waxxd
 * @version 1.0
 * @date 2019-11-12
 **/
@Component
public class DateConverterConfig implements Converter<String, Date> {

    private static final List<String> formarts = new ArrayList<>(4);

    static{
        formarts.add("yyyy-MM");
        formarts.add("yyyy-MM-dd");
        formarts.add("yyyy-MM-dd hh:mm");
        formarts.add("yyyy-MM-dd hh:mm:ss");
        formarts.add("yyyy/MM/dd");
        formarts.add("yyyy.MM.dd");
    }


    @Override
    public Date convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if(source.matches("^\\d{4}-\\d{1,2}$")){
            return parseDate(source, formarts.get(0));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")){
            return parseDate(source, formarts.get(1));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formarts.get(2));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formarts.get(3));
        } else if(source.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")){
            return parseDate(source, formarts.get(4));
        } else if(source.matches("^\\d{4}.\\d{1,2}.\\d{1,2}$")){
            return parseDate(source, formarts.get(5));
        }else {
            throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
        }
    }

    public  Date parseDate(String dateStr, String format) {
        Date date=null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {

        }
        return date;
    }

}
