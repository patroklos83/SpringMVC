package com.patroclos.aop;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.patroclos.model.BaseO;

@Aspect
@Component
public class TraceAspect extends BaseAspect {
	
	@Pointcut("within(* com.patroclos.process..*(..)) || execution(* com.patroclos.service..*(..)) || execution(* com.patroclos.businessobject..*(..))")
	public void pointcutMethod() {}

	@AfterThrowing(pointcut="execution(* com.patroclos.process..*(..)) || execution(* com.patroclos.service..*(..)) || execution(* com.patroclos.businessobject..*(..))", throwing = "e")
	public void logAfterThrowingAllMethods(Exception e) throws Throwable {
		
		String stackTrace = null;
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
			  e.printStackTrace(pw);
			  stackTrace = sw.toString();
			} catch (IOException ex) {
			  throw new RuntimeException("Error while converting the stacktrace");
			}
		
		logger.info("{};\n Error thrown:" 
				+ "\n" + e.getMessage() 
				+ "\n {}", 
				getUniqueThreadHashCode(), 
				stackTrace 
				+ "\nCause: " +  e.getCause());
	}

	@Around("execution(* com.patroclos.process..*(..)) || execution(* com.patroclos.service..*(..)) || execution(* com.patroclos.businessobject..*(..))")	
	public Object logExecutionPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = null;
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		long start = System.currentTimeMillis();
		result = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - start;
		String callStackHash = getUniqueThreadHashCode();
		logger.info("{}; " + signature.toLongString() + " executed in " + executionTime + "ms", callStackHash);
		return result;
	}
	
	@Around("execution(String com.patroclos.controller.page..*Page*(..))")	
	public Object logControllerMetricPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = null;
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		long start = System.currentTimeMillis();
		result = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - start;				
		return result;
	}

	@Before("execution(* com.patroclos.process..*(..)) || execution(* com.patroclos.service..*(..)) || execution(* com.patroclos.businessobject..*(..))")
	public void logTraceInfo(JoinPoint jp) throws JsonProcessingException {
		Signature sig = jp.getSignature();
		MethodSignature signature = (MethodSignature) jp.getSignature();
		String callStackHash = "";
		callStackHash = getUniqueThreadHashCode();
		logger.info("{}; Entering [" + sig.toShortString() + "]", callStackHash);
		printMethodParams(jp.getArgs());
	}

	@AfterReturning(pointcut="execution(* com.patroclos.process..*(..)) || execution(* com.patroclos.service..*(..)) || execution(* com.patroclos.businessobject..*(..))", returning="retVal") 
	public void logReturningParams(JoinPoint jp, Object retVal) throws JsonProcessingException {
		if (retVal != null) {
			String val = retVal.toString();
			if (retVal instanceof BaseO) {
				val = jacksonObjectMapper.writeValueAsString(retVal);
			}
			logger.info("{}; Method returned args values: arg type {} | arg value: " +  val, getUniqueThreadHashCode(), retVal.getClass().getName());
		}
	}
}