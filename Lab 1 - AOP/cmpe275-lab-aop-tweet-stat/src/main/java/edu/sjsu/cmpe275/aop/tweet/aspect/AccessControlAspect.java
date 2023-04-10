package edu.sjsu.cmpe275.aop.tweet.aspect;

import java.security.AccessControlException;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.tweet.TweetStatsServiceImpl;

@Aspect
@Order(0)
public class AccessControlAspect {

	@Autowired
	TweetStatsServiceImpl tweetStatsService;

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.reply(..))")
	public void replyAccessControlAdvice(JoinPoint joinPoint) {

		System.out.printf("reply access control method: %s\n", joinPoint.getSignature().getName());
		String follower = (String) joinPoint.getArgs()[0];
		UUID originalMessage = (UUID) joinPoint.getArgs()[1];
		
		if (tweetStatsService.checkWhetherBlockedUser(originalMessage, follower)
				|| !tweetStatsService.hasMessageAccess(originalMessage, follower))
			throw new AccessControlException(
					"AccessControlException: if the current user has not been shared with the original message or the current user has blocked the original sender.");

	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.like(..))")
	public void likeAccessControlAdvice(JoinPoint joinPoint) {
		System.out.printf("Like access control method: %s\n", joinPoint.getSignature().getName());
		String user = (String) joinPoint.getArgs()[0];
		UUID messageId = (UUID) joinPoint.getArgs()[1];
		System.out.println("user in likeAccessAdvice==>"+user);
		System.out.println("messageId in likeAccessAdvice==>"+messageId);
		if (tweetStatsService.likeAccessCheck(user, messageId))
			throw new AccessControlException(
					"AccessControlException: if the given user has not been successfully shared with the given message, the given message does not exist, someone tries to like his own messages or when the message with the given ID is already successfully liked by the same user.");
	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.report(..))")
	public void reportAccessControlAdvice(JoinPoint joinPoint) {

		System.out.printf("report access control method: %s\n", joinPoint.getSignature().getName());
		String user = (String) joinPoint.getArgs()[0];
		UUID messageId = (UUID) joinPoint.getArgs()[1];
		if (tweetStatsService.reportAccessCheck(user, messageId))
			throw new AccessControlException(
					"AccessControlException: if the given user has not been successfully shared with the given message, the given message does not exist, someone tries to report his own messages or when the message with the given ID is already successfully reported by the same user.");
	}

}
