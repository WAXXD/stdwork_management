package com.stdwork_management.interceptor;

import com.stdwork_management.base.annotation.Token;
import com.stdwork_management.exception.UserDefinedException;
import com.stdwork_management.utils.RedisUtils;
import com.stdwork_management.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-29
 **/
@Slf4j
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtils redisUtils;

    private boolean validateAuthorize(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Token methodAnnotation = handlerMethod.getMethodAnnotation(Token.class);
        if (methodAnnotation != null){
            if (methodAnnotation.requireAuthorize() ){
                String token = request.getHeader("token") == null ? request.getParameter("token") : request.getHeader("token");
                Cookie[] cookies = request.getCookies();
                String tokenFromCookie = "";
                if(cookies != null){
                    for(Cookie cookie : cookies){
                        if (StringUtils.equalsIgnoreCase(cookie.getName(), "token")) {
                            tokenFromCookie = cookie.getValue();
                        }
                    }
                }
                String cachedToken;
                Object user;
                if(StringUtils.equals(methodAnnotation.accountType(), "std")){
//                    cachedToken = (String)request.getSession().getAttribute("token");
//                    user = request.getSession().getAttribute("user");
                    cachedToken =  (String)redisUtils.getAndUpdateExpire(token);
                    user = redisUtils.getAndUpdateExpire("user-" + token);

                } else if (StringUtils.equals(methodAnnotation.accountType(), "admin")){
//                    cachedToken = (String)request.getSession().getAttribute("admin_token");
//                    user = request.getSession().getAttribute("admin_user");
                    cachedToken =  (String) redisUtils.getAndUpdateExpire(token);
                    user = redisUtils.getAndUpdateExpire("admin_user-" + token);
                } else {
                    throw new UserDefinedException(99999999, "您未登录到系统,请登录后访问");
                }

                if(StringUtils.isNotBlank(token) && token != null){
                    if(StringUtils.equals(token,  cachedToken) || StringUtils.equals(cachedToken, tokenFromCookie)){
                        if(user == null){
                            throw new UserDefinedException(99999999, "登录token过期, 请重新登录");
                        }
                        ThreadLocalUtil.put("user", user);
                        return true;
                    } else {
                        throw new UserDefinedException(99999999, "验证失败，非法访问被拒绝");
                    }
                } else {
                    throw new UserDefinedException(99999999, "您未登录到系统,请登录后访问");
                }

            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            log.info("into authorize interceptor...");
            return validateAuthorize(request, response, handler);
        }
        return true;
    }
}
