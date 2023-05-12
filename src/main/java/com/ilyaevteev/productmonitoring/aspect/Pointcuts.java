package com.ilyaevteev.productmonitoring.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {
    @Pointcut(value = "execution(* com.ilyaevteev.productmonitoring.rest.*.*(..))")
    public void controllerPointcut() {}
}
