package edu.sjsu.cmpe275.aop.tweet;

import java.util.UUID;

public interface TweetStatsService {
	// Please do NOT change this file. Refer to the handout for actual definitions.

	void resetStatsAndSystem(); //done

	int getLengthOfLongestTweet(); //done

	String getMostActiveFollower(); //done

	UUID getMostPopularMessage(); //done

	UUID getMostContraversialMessage(); //done

	String getMostUnpopularFollower(); //done
	
	int getMaximumMessageFanout(); //done
}