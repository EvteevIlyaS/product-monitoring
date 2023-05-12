package com.ilyaevteev.productmonitoring.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ControllerLoggingAspect {
    @Around("com.ilyaevteev.productmonitoring.aspect.Pointcuts.controllerPointcut()")
    public Object controllerLoggingAdvice(ProceedingJoinPoint pjp) throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().toString();

        log.info("Method invoked - " + className + " : \"" + methodName + "\"");
        Object res = pjp.proceed();
        log.info("Method \"" + methodName + "\" response : " + mapper.writeValueAsString(res));

        return res;
    }
}
