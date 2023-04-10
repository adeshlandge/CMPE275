package edu.sjsu.cmpe275.aop.tweet.aspect;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.tweet.TweetStatsServiceImpl;

@Aspect
@Order(2)
public class StatsAspect {

	@Autowired
	TweetStatsServiceImpl tweetStatsService;

	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.tweet(..))", returning = "result")
	public void afterTweet(JoinPoint joinPoint, Object result) {
		System.out.println("afterTweet Aspect");
		String user = (String) joinPoint.getArgs()[0];
		String tweetMessage = (String) joinPoint.getArgs()[1];
		UUID messageId = (UUID) result;
		tweetStatsService.handletweet(user, tweetMessage, messageId);
	}

	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.reply(..))", returning = "result")
	public void afterReply(JoinPoint joinPoint, Object result) {
		String user = (String) joinPoint.getArgs()[0];
		UUID originalMessageId = (UUID) joinPoint.getArgs()[1];
		String replyMessage = (String) joinPoint.getArgs()[2];
		UUID replyMessageId = (UUID) result;
		tweetStatsService.handleReply(replyMessageId, user, originalMessageId, replyMessage);
	}

	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.follow(..))")
	public void afterFollow(JoinPoint joinPoint) {
		String follower = (String) joinPoint.getArgs()[0];
		String followee = (String) joinPoint.getArgs()[1];
		tweetStatsService.follow(follower, followee);
	}

	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.block(..))")
	public void afterBlock(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		String follower = (String) joinPoint.getArgs()[1];
		tweetStatsService.block(user, follower);
	}

	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.like(..))")
	public void afterLike(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		UUID messageId = (UUID) joinPoint.getArgs()[1];
		tweetStatsService.like(user, messageId);
	}

	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.report(..))")
	public void afterReport(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		UUID messageId = (UUID) joinPoint.getArgs()[1];
		tweetStatsService.report(user, messageId);
	}

}
