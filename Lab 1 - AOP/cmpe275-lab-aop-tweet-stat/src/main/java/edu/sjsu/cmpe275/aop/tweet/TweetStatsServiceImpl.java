package edu.sjsu.cmpe275.aop.tweet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class TweetStatsServiceImpl implements TweetStatsService {

	private String longestTweet = null;

	private String mostFollowedUser = null;
	private int mostFollowedUserCounter = 0;

	private UUID mostPopularMessage = null;
	private int popularityCounter = 0;

	private String mostUnpopularFollower = null;
	private int unpopularityCounter = -1;

	private UUID maxMessageFanoutThread = null; // to store message with longest replies thread
	private int maxMessageFanout = -1; // to store the count of longest replies thread to a message

	HashMap<String, HashSet<String>> follows = new HashMap<>();
	HashMap<String, HashSet<String>> blocksMap = new HashMap<>();
	HashMap<UUID, HashSet<String>> likesMap = new HashMap<>();
	HashMap<UUID, HashSet<String>> reportsMap = new HashMap<>();
	HashMap<UUID, Integer> replyMessageCounterMap = new HashMap<>(); // to store the # of replies per message

	HashMap<String, HashSet<String>> followeeFollowerMap = new HashMap<>();// to store followee - followersList Map when
																			// an user follows

	HashMap<UUID, HashSet<String>> messageFollowerAccessMap = new HashMap<>();// to store followers per message
																				// (tweet/reply)

	HashMap<String, Integer> userToReplyLength = new HashMap<>();

	HashMap<UUID, String> messageUserMap = new HashMap<>(); // to store user - tweet or reply mapping

	HashMap<String, HashSet<String>> followerMap = new HashMap<>();
	HashSet<UUID> messagesSet = new HashSet<UUID>();
	HashMap<UUID, Integer> messageLikesMap = new HashMap<>();
	HashMap<UUID, Integer> messageReportsMap = new HashMap<>();

	@Override
	public void resetStatsAndSystem() {
		longestTweet = null;
		mostFollowedUser = null;
		mostFollowedUserCounter = 0;
		mostPopularMessage = null;
		popularityCounter = 0;
		mostUnpopularFollower = null;
		unpopularityCounter = -1;
		maxMessageFanoutThread = null;
		maxMessageFanout = -1;
		follows = new HashMap<>();
		blocksMap = new HashMap<>();
		likesMap = new HashMap<>();
		replyMessageCounterMap = new HashMap<>();
		followeeFollowerMap = new HashMap<>();
		messageFollowerAccessMap = new HashMap<>();

		userToReplyLength = new HashMap<>();
		messageUserMap = new HashMap<>();

		followerMap = new HashMap<>();
		messagesSet = new HashSet<>();
		messageLikesMap = new HashMap<>();
		messageReportsMap = new HashMap<>();
	}

	@Override
	public int getLengthOfLongestTweet() {
		// the length of longest message (tweet or reply) a user successfully sent since
		// the beginning or last reset.
		if (this.longestTweet == null) {
			return 0;
		} else {
			return this.longestTweet.length();
		}
	}

	@Override
	public UUID getMostPopularMessage() {
		return mostPopularMessage;

	}

	@Override
	public String getMostUnpopularFollower() {
		// the user that has been blocked by most no.of users.
		return mostUnpopularFollower;
	}

	@Override
	public String getMostActiveFollower() {
		// the user that has followed many different users.
		String mostActiveFollower = null;
		int maxFollowees = 0;
		for (String key : followerMap.keySet()) {
			HashSet<String> values = followerMap.get(key);
			int count = values.size();
			if (count > maxFollowees || (count == maxFollowees && key.compareTo(mostActiveFollower) < 0)) {
				mostActiveFollower = key;
				maxFollowees = count;
			}
		}
		return mostActiveFollower;
	}

	@Override
	public UUID getMostContraversialMessage() {
		// Message with Like and Report ((L+R)^2)/((L-R)^2 +1)
		UUID contraversialMessage = null;
		Double contraversialityScore = 0.000;
		for (UUID messageId : messagesSet) {
			if (!messageLikesMap.containsKey(messageId) || !messageReportsMap.containsKey(messageId)) {
				// if there are either zero likesMap or zero reports for a message then score is
				// 0.
				continue;
			}
			if (messageLikesMap.containsKey(messageId) || messageReportsMap.containsKey(messageId)) {
				int likesMap = messageLikesMap.get(messageId);
				int reports = messageReportsMap.get(messageId);
				double score = (double) ((likesMap + reports) ^ 2 / ((likesMap - reports) ^ 2 + 1));
				if (score > contraversialityScore
						|| (score == contraversialityScore && messageId.compareTo(messageId) < 0)) {
					contraversialMessage = messageId;
					contraversialityScore = score;
				}
			}
		}

		return contraversialMessage;
	}

	@Override
	public int getMaximumMessageFanout() {
		return maxMessageFanout;
	}

	private void setMostPopularMessage(UUID tweet) {
		if (popularityCounter < messageFollowerAccessMap.get(tweet).size()
				|| (mostPopularMessage != null && popularityCounter == messageFollowerAccessMap.get(tweet).size()
						&& mostPopularMessage.compareTo(tweet) > 0)) {
			popularityCounter = messageFollowerAccessMap.get(tweet).size();
			mostPopularMessage = tweet;
		}
	}

	private void setMaximumMessageFanout(UUID reply) {
		if ((replyMessageCounterMap.get(reply) > maxMessageFanout)
				|| (replyMessageCounterMap.get(reply) == maxMessageFanout
						&& maxMessageFanoutThread.compareTo(reply) > 0)) {
			maxMessageFanout = replyMessageCounterMap.get(reply);
			maxMessageFanoutThread = reply;
		}

	}

	private void setLongestTweet(String message) {
		if (longestTweet == null || message.length() > longestTweet.length()
				|| (message.length() == longestTweet.length() && message.compareTo(longestTweet) > 0))
			longestTweet = message;
	}

	public void handletweet(String user, String message, UUID tweet) {
		messageUserMap.put(tweet, user);
		messageFollowerAccessMap.put(tweet, new HashSet<String>());
		replyMessageCounterMap.put(tweet, 1);

		if (followeeFollowerMap.containsKey(user)) {
			messageFollowerAccessMap.get(tweet).addAll(followeeFollowerMap.get(user));
		}

		messagesSet.add(tweet);

		setLongestTweet(message);
		setMaximumMessageFanout(tweet);
		setMostPopularMessage(tweet);
	}

	public void handleReply(UUID reply, String user, UUID originalMessage, String message) {
		messageUserMap.put(reply, user);
		replyMessageCounterMap.put(reply, replyMessageCounterMap.get(originalMessage) + 1);

		setMaximumMessageFanout(reply); // adding to message fanout thread.

		messageFollowerAccessMap.put(reply, new HashSet<String>());
		if (followeeFollowerMap.containsKey(user)) {
			messageFollowerAccessMap.get(reply).addAll(followeeFollowerMap.get(user));
		}
		messageFollowerAccessMap.get(reply).add(getUserOfThisMessage(originalMessage));

		setMostPopularMessage(reply);
		setLongestTweet(message);

		int len = 0;
		if (userToReplyLength.containsKey(user)) {
			len = userToReplyLength.get(user);
		}
		userToReplyLength.put(user, len + message.length());

		messagesSet.add(reply);
	}

	public void follow(String follower, String followee) {
		HashSet<String> temp = new HashSet<String>();
		if (follows.containsKey(followee)) {
			temp = follows.get(followee);
		}

		temp.add(follower);
		follows.put(followee, temp);
		if (temp.size() > mostFollowedUserCounter) {
			mostFollowedUserCounter = temp.size();
			mostFollowedUser = followee;
		} else if (temp.size() == mostFollowedUserCounter && mostFollowedUser.compareTo(followee) > 0) {
			mostFollowedUser = followee;
		}
		if (blocksMap.containsKey(follower) && blocksMap.get(follower).contains(followee)) {
			return;
		}
		temp = new HashSet<String>();
		if (followeeFollowerMap.containsKey(followee)) {
			temp = followeeFollowerMap.get(followee);
		}

		temp.add(follower);
		followeeFollowerMap.put(followee, temp);

		HashSet<String> tempFolloweeSet = (!followerMap.containsKey(follower)) ? new HashSet<String>()
				: followerMap.get(follower);
		tempFolloweeSet.add(followee);
		followerMap.put(follower, tempFolloweeSet);
	}

	public void block(String user, String follower) {
		HashSet<String> temp = new HashSet<String>();
		if (blocksMap.containsKey(follower)) {
			temp = blocksMap.get(follower);
		}

		temp.add(user);
		blocksMap.put(follower, temp);
		if (temp.size() > unpopularityCounter) {
			unpopularityCounter = temp.size();
			mostUnpopularFollower = follower;
		} else if (temp.size() == unpopularityCounter && mostUnpopularFollower.compareTo(follower) > 0)
			mostUnpopularFollower = follower;
		if (followeeFollowerMap.containsKey(user))
			followeeFollowerMap.get(user).remove(follower);
	}

	public void like(String user, UUID messageId) {
		HashSet<String> temp = new HashSet<String>();
		if (likesMap.containsKey(messageId)) {
			temp = likesMap.get(messageId);
		}
		temp.add(user);
		likesMap.put(messageId, temp);

		if (!messageLikesMap.containsKey(messageId)) {
			messageLikesMap.put(messageId, 1);
		} else {
			messageLikesMap.put(messageId, messageLikesMap.get(messageId) + 1);
		}
	}
	
	public boolean likeAccessCheck(String user, UUID messageID) {
		System.out.println("messageUserMap:"+messageUserMap);
		System.out.println("messageFollowerAccessMap:"+messageFollowerAccessMap);
		System.out.println("likesMap:"+likesMap);
		return !messageUserMap.containsKey(messageID) || messageUserMap.get(messageID).equals(user)
				|| !messageFollowerAccessMap.get(messageID).contains(user)
				|| (likesMap.containsKey(messageID) && likesMap.get(messageID).contains(user));
	}

	public void report(String user, UUID messageId) {
		HashSet<String> temp = new HashSet<String>();
		if (reportsMap.containsKey(messageId)) {
			temp = reportsMap.get(messageId);
		}
		temp.add(user);
		reportsMap.put(messageId, temp);

		if (!messageReportsMap.containsKey(messageId)) {
			messageReportsMap.put(messageId, 1);
		} else {
			messageReportsMap.put(messageId, messageReportsMap.get(messageId) + 1);
		}

	}

	public boolean reportAccessCheck(String user, UUID messageID) {
		return !messageUserMap.containsKey(messageID) || messageUserMap.get(messageID).equals(user)
				|| !messageFollowerAccessMap.get(messageID).contains(user)
				|| (reportsMap.containsKey(messageID) && reportsMap.get(messageID).contains(user));
	}
	
	public boolean checkWhetherBlockedUser(UUID originalMessage, String follower) {
		String followee = messageUserMap.get(originalMessage);
		return blocksMap.containsKey(followee) && blocksMap.get(followee).contains(follower);
	}

	public boolean hasMessageAccess(UUID originalMessage, String user) {
		return messageFollowerAccessMap.get(originalMessage).contains(user);
	}

	public boolean containsMessage(UUID message) {
		return messageUserMap.containsKey(message);
	}

	public String getUserOfThisMessage(UUID message) {
		return messageUserMap.get(message);
	}

}
