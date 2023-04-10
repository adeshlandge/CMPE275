package edu.sjsu.cmpe275.aop.tweet.aspect;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.tweet.TweetStatsServiceImpl;

@Aspect
@Order(3)
public class ValidationAspect {

	@Autowired
	TweetStatsServiceImpl tweetStatsService;

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.tweet(..))")
	public void tweetAdvice(JoinPoint joinPoint) {
		System.out.printf("Validation to check the arguments for the tweet() method %s\n",
				joinPoint.getSignature().getName());
		Object[] args = joinPoint.getArgs();
		String user = (String) args[0];
		String message = (String) args[1];
		if (user == null || user.trim().isEmpty() || message == null || message.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"IllegalArgumentException: Username or message cannot be empty, in method: "
							+ joinPoint.getSignature().getName());
		}
		if (message.length() > 140) {
			throw new IllegalArgumentException(
					"IllegalArgumentException: Message is more than 140 characters,  in method: "
							+ joinPoint.getSignature().getName());
		}
	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.reply(..))")
	public void replyAdvice(JoinPoint joinPoint) {
		System.out.printf("Validation to check the arguments for the reply() method %s\n",
				joinPoint.getSignature().getName());
		
		Object[] args = joinPoint.getArgs();
		String user = (String) args[0];
		UUID originalMessage = (UUID) args[1];
		String message = (String) args[2];
		if (user == null || originalMessage == null || message == null || user.trim().isEmpty()
				|| originalMessage.toString().trim().isEmpty() || message.trim().isEmpty())
			throw new IllegalArgumentException(
					"IllegalArgumentException: user or originalMessage or message is null/empty in method: "
							+ joinPoint.getSignature().getName());

		if (!tweetStatsService.containsMessage(originalMessage)
				|| tweetStatsService.getUserOfThisMessage(originalMessage).equals(user) || message.length() > 140)
			throw new IllegalArgumentException(
					"IllegalArgumentException: user is trying to reply to his own message in method: "
							+ joinPoint.getSignature().getName());

	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.follow(..))")
	public void followAdvice(JoinPoint joinPoint) {
		System.out.printf("Validation to check the arguments for the method %s\n",
				joinPoint.getSignature().getName());
		Object[] args = joinPoint.getArgs();
		String follower = (String) args[0];
		String followee = (String) args[1];
		if (follower == null || follower.trim().isEmpty() || followee == null || followee.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"IllegalArgumentException: Follower or Followee cannot be empty, in method: "
							+ joinPoint.getSignature().getName());
		} else if (follower.equals(followee)) {
			throw new IllegalArgumentException(
					"IllegalArgumentException: follower is trying to follow himself/herself in"
							+ joinPoint.getSignature().getName());
		}
	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.block(..))")
	public void blockAdvice(JoinPoint joinPoint) {
		System.out.printf("Validation to check the arguments for the method %s\n",
				joinPoint.getSignature().getName());

		Object[] args = joinPoint.getArgs();
		String user = (String) args[0];
		String followee = (String) args[1];
		if (user == null || user.trim().isEmpty() || followee == null || followee.trim().isEmpty()) {
			throw new IllegalArgumentException("IllegalArgumentException: User or Followee cannot be empty, in method: "
					+ joinPoint.getSignature().getName());
		} else if (user.equals(followee)) {
			throw new IllegalArgumentException("IllegalArgumentException: user is trying to block himself/herself in"
					+ joinPoint.getSignature().getName());
		}
	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.like(..))")
	public void likeAdvice(JoinPoint joinPoint) {
		System.out.printf("Validation to check the arguments for the method %s\n",
				joinPoint.getSignature().getName());
		Object[] args = joinPoint.getArgs();
		String user = (String) args[0];
		UUID messageId = null;
		if (args[1] instanceof UUID) {
			messageId = (UUID) args[1];
		}
		if (user == null || user.trim().isEmpty() || messageId == null || messageId.toString().trim().isEmpty()) {
			throw new IllegalArgumentException(
					"IllegalArgumentException: User or MessageID cannot be empty in order to like a message, in method: "
							+ joinPoint.getSignature().getName());
		}
	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.report(..))")
	public void reportAdvice(JoinPoint joinPoint) {
		System.out.printf("Validation to check the arguments for the method %s\n",
				joinPoint.getSignature().getName());
		Object[] args = joinPoint.getArgs();
		String user = (String) args[0];
		UUID messageId = null;
		if (args[1] instanceof UUID) {
			messageId = (UUID) args[1];
		}
		if (user == null || user.trim().isEmpty() || messageId == null || messageId.toString().trim().isEmpty()) {
			throw new IllegalArgumentException(
					"IllegalArgumentException: User or MessageID cannot be empty in order to report a message, in method: "
							+ joinPoint.getSignature().getName());
		}
	}

}
