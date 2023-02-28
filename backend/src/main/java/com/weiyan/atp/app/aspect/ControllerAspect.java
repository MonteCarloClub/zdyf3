package com.weiyan.atp.app.aspect;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ControllerAspect {

    //切入点描述 这个是controller包的切入点
    @Pointcut("execution(public * com.weiyan.atp.app.controller..*.*(..))")
    public void controllerEntrance() {
        // 签名，可以理解成这个切入点的一个名称
    }
}
