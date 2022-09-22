package com.patroclos.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect extends BaseAspect {
	
	@Before("within(com.patroclos.controller.summary.ArticlesController)")
	public void logBefore(JoinPoint jp) {
	}
	
	@Around("execution(* com.patroclos.uicomponent.UISummaryForm.draw(..))")
	public Object logAfter(ProceedingJoinPoint point) throws Throwable {		
		return point.proceed();
	}
	
	@Around("execution(* com.patroclos.uicomponent.UITable.*(..))")
    public Object advice(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

}
