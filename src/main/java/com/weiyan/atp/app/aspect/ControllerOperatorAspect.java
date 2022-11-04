package com.weiyan.atp.app.aspect;

import com.weiyan.atp.data.bean.ControllerLog;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
public class ControllerOperatorAspect {
    private static final ThreadLocal<ControllerLog> LOG_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * controller处理之前将operator写入ThreadLocal
     */
    @Before("com.weiyan.atp.app.aspect.ControllerAspect.controllerEntrance()")
    public void onEnter(JoinPoint joinPoint) {
        HttpServletRequest request = getRequest();
        ControllerLog logInfo = getEnterLog(request, joinPoint);
        LOG_THREAD_LOCAL.set(logInfo);
        log.info("Entering controller={} ", logInfo.toString());
    }

    /**
     * controller返回之后获取日志一些基本信息.
     */
    @AfterReturning(returning = "ret", pointcut = "com.weiyan.atp.app.aspect.ControllerAspect.controllerEntrance()")
    public void onReturn(Object ret) {
        ControllerLog logInfo = LOG_THREAD_LOCAL.get();
        enrichReturnLog(logInfo, ret);
        LOG_THREAD_LOCAL.remove();
        log.info("Returning from controller={} ", logInfo.toString());
    }

    /**
     * controller异常之后获取日志一些基本信息.
     */
    @AfterThrowing(throwing = "ex", pointcut = "com.weiyan.atp.app.aspect.ControllerAspect.controllerEntrance()")
    public void onThrow(JoinPoint jp, Throwable ex) {
        ControllerLog logInfo = LOG_THREAD_LOCAL.get();
        enrichReturnLog(logInfo, null);
        LOG_THREAD_LOCAL.remove();
        log.warn("Throwing from controller={} ", logInfo.toString(), ex);
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    private static ControllerLog getEnterLog(HttpServletRequest request, JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ControllerLog logInfo = new ControllerLog();
        logInfo.setArgs(Arrays.toString(joinPoint.getArgs()));
        logInfo.setClassMethod(joinPoint.getSignature().getDeclaringTypeName()
            + '.' + joinPoint.getSignature().getName());
        logInfo.setIp(request.getRemoteAddr());
        logInfo.setHttpMethod(request.getMethod());
        logInfo.setUrl(request.getRequestURL().toString());
        logInfo.setStartTime(System.currentTimeMillis());

        enrichUser(logInfo);
        return logInfo;
    }

    private static void enrichUser(ControllerLog logInfo) {
        // todo add user
        logInfo.setUser(null);
    }

    private static void enrichReturnLog(ControllerLog logInfo, Object result) {
        logInfo.setEndTime(System.currentTimeMillis());
        logInfo.setSpendTime(System.currentTimeMillis() - logInfo.getStartTime());
        if (result != null) {
            logInfo.setResult(result.toString());
        }
    }

}
