package edu.sjsu.cmpe275.aop.tweet.aspect;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.aspectj.lang.annotation.Around;

@Aspect
@Order(1)
public class RetryAspect {

	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.*(..))")
	public Object retryAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.printf("Retry Advice of the method: %s\n", joinPoint.getSignature().getName());
		int retryCount = 0;
		Object resultMethod = null;
		while (true) {
			try {
				resultMethod = joinPoint.proceed();
				System.out.print("Successful execution of: " + joinPoint.getSignature().getName());
				break;
			} catch (IOException ex) {
				System.out.println(
						"Trying to run the method: " + joinPoint.getSignature().getName() +" "+ retryCount + " again!");
				if (retryCount == 4) {
					System.out.println("Network Failure in: " + joinPoint.getSignature().getName() + " Retry Counter: "
							+ retryCount);
					ex.printStackTrace();
					throw ex;
				}
				retryCount++;
			}
		}
		return resultMethod;
	}
}
