package edu.sjsu.cmpe275.aop.tweet;

import java.util.UUID;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main(String[] args) {
		/***
		 * Following is a dummy implementation of App to demonstrate bean creation with
		 * Application context. You may make changes to suit your need, but this file is
		 * NOT part of the submission.
		 */

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
		System.out.println("stx==>" + ctx);
		TweetService tweeter = (TweetService) ctx.getBean("tweetService");
		TweetStatsService stats = (TweetStatsService) ctx.getBean("tweetStatsService");

		try {
			tweeter.follow("bob", "alice");
			UUID msg = tweeter.tweet("alice", "first tweet");
			UUID reply = tweeter.reply("bob", msg,
					"Good!");
			System.out.println("msg==>" + msg);
			System.out.println("reply==>" + reply);
			tweeter.like("bob", msg);
			tweeter.follow("Parley", "alice");
			tweeter.reply("Parley", msg, "good start");
			tweeter.like("Parley", msg);
			
			/*
			tweeter.follow("adesh", "alice");
			tweeter.report("adesh", msg);
			
			tweeter.like("alice", reply);
			tweeter.report("adesh", reply);
			tweeter.report("Shelby", reply);*/
			
			System.out.println("Length of the longest tweet: " + stats.getLengthOfLongestTweet());
			System.out.println("Most popular message: " + stats.getMostPopularMessage());
			System.out.println("Maximum fanout: " + stats.getMaximumMessageFanout());
			System.out.println("Unpopular follower : " + stats.getMostUnpopularFollower());
			System.out.println("Most active follower : " + stats.getMostActiveFollower());
			System.out.println("contro-->" + stats.getMostContraversialMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		ctx.close();
	}
}
