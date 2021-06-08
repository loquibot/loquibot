package com.alphalaneous;

public class Comment {

	private final String username;
	private final String comment;
	private final String likes;
	private final String percent;

	Comment(String username, String comment, String likes, String percent) {
		this.username = username;
		this.comment = comment;
		this.likes = likes;
		this.percent = percent;
	}

	public String getUsername() {
		return String.valueOf(username);
	}

	public String getComment() {
		return String.valueOf(comment);
	}

	public String getLikes() {
		return String.valueOf(likes);
	}

	public String getPercent() {
		return String.valueOf(percent);
	}
}
