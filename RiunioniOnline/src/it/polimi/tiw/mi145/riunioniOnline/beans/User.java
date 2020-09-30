package it.polimi.tiw.mi145.riunioniOnline.beans;

import java.util.List;

public class User {
	private final Integer id;
	private final String userName;
	private final String password;
	private final List<Integer> ownMeetings;
	private final List<Integer> otherMeetings;

	public User(Integer id, String userName, String password, List<Integer> ownMeetings, List<Integer> otherMeetings) {
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.ownMeetings = ownMeetings;
		this.otherMeetings = otherMeetings;
	}

	public int getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public List<Integer> getOwnMeetings() {
		return ownMeetings;
	}

	public List<Integer> getOtherMeetings() {
		return otherMeetings;
	}
}
