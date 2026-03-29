package com.blog.Interceptor;

import com.blog.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String header = request.getHeader("Authorization");
        if(header==null || !header.startsWith("Bearer ")){
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"未登录\"}");
            return false;
        }

        String token=header.substring(7);
        if(!JwtUtil.validateToken(token)){
            response.setStatus(401);
            response.setContentType("application/json:charset=UTF-8");
            response.getWriter().write("{\"error\":\"token无效或已过期\"}");
            return false;
        }

        String username=JwtUtil.parseToken(token);
        request.setAttribute("username",username);
        return true;

    }


}
