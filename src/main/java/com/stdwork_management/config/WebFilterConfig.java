package com.stdwork_management.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-09-12
 **/
@Configuration
public class WebFilterConfig {

    @Bean
    public FilterRegistrationBean validatorFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter( new HttpRequestFilter());

        registration.addUrlPatterns("/*");
        registration.setOrder(Integer.MAX_VALUE-10);
        return registration;
    }
}
