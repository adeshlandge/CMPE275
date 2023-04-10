package edu.sjsu.cmpe275.aop.tweet;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class TweetServiceImpl implements TweetService {

	/***
	 * Following is a dummy implementation, which the correctness of your submission
	 * cannot depend on. You can tweak the implementation to suit your need, but
	 * this file is NOT part of the submission.
	 */

	// to map messageId with user.
	private Map<UUID, String> messages;

	private Map<String, Set<String>> blocks;

	public TweetServiceImpl() {
		messages = new HashMap<UUID, String>();
		blocks = new HashMap<String, Set<String>>();
	}

	@Override
	public UUID tweet(String user, String message) throws IllegalArgumentException, IOException {
		if (message.length() > 140) {
			throw new IllegalArgumentException("length of the message is more than 140 characters.");
		}
		if (user == null || user.isEmpty() || message == null || message.isEmpty()) {
			throw new IllegalArgumentException("User cannot be null or empty");
		}
		if (message == null || message.isEmpty()) {
			throw new IllegalArgumentException("Message cannot be null or empty");
		}

		System.out.printf("User %s tweeted message: %s\n", user, message);
		UUID messageId = UUID.randomUUID();
		messages.put(messageId, message);
		return messageId;
	}

	@Override
	public UUID reply(String user, UUID originalMessage, String message) throws IOException, IOException {
		System.out.printf("User %s tweeted replied to message %s with message: %s\n", user, originalMessage, message);
		return UUID.randomUUID();
	}

	@Override
	public void follow(String follower, String followee) throws IOException {
		Random rand = new Random();
		int i = rand.nextInt(5) + 1;
		if (i == 1) {
			throw new IOException(follower + " following " + followee + " failed!");
		} else {
			System.out.printf("User %s followed user %s \n", follower, followee);
		}
	}

	@Override
	public void block(String user, String follower) throws IOException {
		Random rand = new Random();
		int i = rand.nextInt(5) + 1;
		if (i == 1) {
			throw new IOException("Sorry, " + user + ".Couldn't block " + follower + " Please try again!\n");
		} else {
			System.out.printf("User %s blocked user %s \n", user, follower);
		}
	}

	@Override
	public void like(String user, UUID messageId) throws AccessControlException, IllegalArgumentException, IOException {
		if (blocks.containsKey(user) && blocks.get(user).contains(messages.get(messageId))) {
			throw new AccessControlException("User is blocked from liking this message");
		}
		System.out.println("Like method: User -  "+user+"UUID: "+messageId);
		System.out.printf("User %s liked message with ID %s \n", user, messageId);
	}

	@Override
	public void report(String user, UUID messageId)
			throws AccessControlException, IllegalArgumentException, IOException {
		System.out.printf("User %s reported message with ID %s \n", user, messageId);
	}
}
