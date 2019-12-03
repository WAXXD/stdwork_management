package com.stdwork_management.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-07-19
 **/
@Slf4j
public class PropertiesUtils {
    public static Properties prop = null;

    public static Properties getProperties(String path){
        Properties prop = null;
        try {
            InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(path);
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    inputStream, "utf-8"));
            prop = new Properties();
            prop.load(bf);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;

    }


    public static String getValue(String path, String key){
        return getProperties(path).getProperty(key);
    }

    public static void main(String[] args) {
//		System.out.println(prop.getProperty("MainFrame.title"));
//		setSkinProper("skin","default");
        Properties properties = getProperties("deviceControl.properties");
        String property = properties.getProperty("face.controlParams.personnelCreation");
        System.out.println(property);
        System.out.println(String.format(property,"s","s","sss"));

    }

}
