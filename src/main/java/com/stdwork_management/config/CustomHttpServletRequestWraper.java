package com.stdwork_management.config;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-09-12
 **/
public class CustomHttpServletRequestWraper extends HttpServletRequestWrapper {

    private Map<String, String[]> params = new HashMap<>();

    public CustomHttpServletRequestWraper(HttpServletRequest request) {
        super(request);
        this.params.putAll(request.getParameterMap());
        modifyParameterValues();
    }

    private void modifyParameterValues() {
        Set<Map.Entry<String, String[]>> entries = params.entrySet();
        for(Map.Entry entry : entries){
            String key = (String) entry.getKey();
            String[] values = params.get(key);
            if(values.length == 1 && StringUtils.isBlank(values[0])){
                params.put(key, null);
            }
        }
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if(values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }
}
