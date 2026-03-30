package com.blog.Interceptor;

import com.blog.annotation.NoAuth;
import com.blog.utils.JwtUtil;
import com.blog.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;

        //放行注解
        if(method.getMethodAnnotation(NoAuth.class)!=null ||
        method.getBeanType().isAnnotationPresent(NoAuth.class)){
            return true;
        }

        String header = request.getHeader("Authorization");
        if(header==null || !header.startsWith("Bearer ")){
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"未登录\"}");
            return false;
        }

        String token=header.substring(7);
        //删除原来的token无效检测，新增token续期
        try {

            Claims claims =JwtUtil.parseToken(token);
            Long userId=Long.valueOf(claims.getSubject());
            UserContext.setUserId(userId);

            //新增续期方法
            if(JwtUtil.isExpireSoon(claims)){
                String newToken=JwtUtil.generateToken(userId);
                response.setHeader("new-token",newToken);
            }

            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"token已经无效\"}");
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
