package com.blog.aop;

import com.blog.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    //获取当前类的日志记录器
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private HttpServletRequest request;

    @Pointcut("execution(* com.blog.controller..*(..))")
    public void pointcut(){
    }

    @Around("pointcut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable{
        long start=System.currentTimeMillis();
        String uri=request.getRequestURI();
        String method=request.getMethod();
        Object args=joinPoint.getArgs();
        Long userId= UserContext.getUserId();

        log.info("====请求开始====");
        log.info("请求路径 {} {}",method,uri);
        log.info("用户id {}",userId);
        log.info("请求参数 {}",args);

        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        }finally {
            long end = System.currentTimeMillis();
            log.info("响应结果 {}",result);
            log.info("耗时 {}ms",end-start);
            log.info("====服务结束====/n");
        }
    }
}
