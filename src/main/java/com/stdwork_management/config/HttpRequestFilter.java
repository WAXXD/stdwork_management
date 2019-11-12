package com.stdwork_management.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-09-12
 **/
public class HttpRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        CustomHttpServletRequestWraper requestWrapper = new CustomHttpServletRequestWraper((HttpServletRequest) request);

        chain.doFilter(requestWrapper, response);
    }
}
