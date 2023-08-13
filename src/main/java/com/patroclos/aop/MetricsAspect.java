package com.patroclos.aop;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.patroclos.metrics.MetricsServlet;
import io.micrometer.core.instrument.Timer;

@Aspect
@Component
public class MetricsAspect extends BaseAspect {

	@Around("execution(* com.patroclos.process..*(..)) || execution(* com.patroclos.service..*(..)) || execution(* com.patroclos.businessobject..*(..))")	
	public Object logExecutionPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = null;
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		long start = System.currentTimeMillis();
		result = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - start;
		String callStackHash = getUniqueThreadHashCode();
		logger.info("{}; " + signature.toLongString() + " executed in " + executionTime + "ms", callStackHash);
	
		Timer timer = Timer
				  .builder("performance.load.timer")
				  .description("time took to ran a process/service/BusinessObject method")
				  .tags("method Name", signature.getMethod().getName())
				  .tags("class Name", signature.getDeclaringTypeName())
				  .publishPercentiles(0.3, 0.5, 0.95)
				  .publishPercentileHistogram()
				  .register(MetricsServlet.registry);
		timer.record(Duration.ofMillis(executionTime).getSeconds(), TimeUnit.SECONDS);
		
		return result;
	}
	
	@Around("execution(String com.patroclos.controller.page..*Page*(..))")	
	public Object logControllerMetricPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = null;
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		long start = System.currentTimeMillis();
		result = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - start;
		
		Timer timerHistogram = Timer
				  .builder("page.load.timer")
				  .description("time to load a record")
				  .tags("method name", signature.getMethod().getName())
				  .tags("controller name", signature.getDeclaringTypeName())
				  .publishPercentiles(0.3, 0.5, 0.95)
				  .publishPercentileHistogram()
				  .register(MetricsServlet.registry);
		timerHistogram.record(Duration.ofMillis(executionTime).getSeconds(), TimeUnit.SECONDS);
		
		Timer timer = Timer
				  .builder("page.load.timer.aggregates")
				  .description("time to load a record by aggregating and getting [sum load times, total call count, max load time] from all requests")
				  .register(MetricsServlet.registry);
		timer.record(Duration.ofMillis(executionTime).getSeconds(), TimeUnit.SECONDS);
				
		return result;
	}
}