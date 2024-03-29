package com.stdwork_management.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stdwork_management.interceptor.AuthorizeInterceptor;
import com.stdwork_management.utils.PropertiesUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-30
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


//    @Autowired
//    private RequestMappingHandlerAdapter handlerAdapter;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        /*放行swagger*/
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/file/**").addResourceLocations("file:" + PropertiesUtils.getValue("config.properties", "server.workpath"));
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.
                addInterceptor(authorizeInterceptor()).
                addPathPatterns("/**").
                excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }

    @Bean
    public AuthorizeInterceptor authorizeInterceptor(){
        return new AuthorizeInterceptor();
    }

//    @PostConstruct
//    public void initEditableAvlidation() {
//        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer)handlerAdapter.getWebBindingInitializer();
//        if(initializer.getConversionService()!=null) {
//            GenericConversionService genericConversionService = (GenericConversionService)initializer.getConversionService();
//            genericConversionService.addConverter(new DateConverterConfig());
//            genericConversionService.addConverter(new StringToStringConvert());
//
//        }
//    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new DateConverterConfig());
        registry.addConverter(new StringToStringConvert());
    }

    //    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
//        converters.add(gsonHttpMessageConverter());
//        super.extendMessageConverters(converters);
//    }

//    @Bean
//    public GsonHttpMessageConverter gsonHttpMessageConverter(){
//        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
//        converter.setGson(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create());
//        return converter;
//    }



    @Bean
    public Gson gson(){
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    @Bean("workPath")
    public String getServerWorkPath(){
        return PropertiesUtils.getValue("config.properties", "server.workpath");
    }



}