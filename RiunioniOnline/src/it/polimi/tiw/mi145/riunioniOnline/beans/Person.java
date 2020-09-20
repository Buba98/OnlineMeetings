package it.polimi.tiw.mi145.riunioniOnline.beans;

import java.util.List;
import org.json.JSONObject;

public class Person {
	private final Integer id;
	private final String userName;
	private final String password;
	private final List<Integer> ownMeetings;
	private final List<Integer> otherMeetings;

	public Person(Integer id, String userName, String password, List<Integer> ownMeetings, List<Integer> otherMeetings) {
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

	public String toJson() {
		
		JSONObject ownMeetingsJson = new JSONObject();
		int i = 0;
		for (Integer id : ownMeetings) {
			ownMeetingsJson.put(String.valueOf(i), id.toString());
			i++;
		}
		
		JSONObject otherMeetingsJson = new JSONObject();
		i = 0;
		for (Integer id : otherMeetings) {
			otherMeetingsJson.put(String.valueOf(i), id.toString());
			i++;
		}
		
		return new JSONObject()
				.put("id", id.toString())
				.put("userName", userName)
				.put("ownMeetings", ownMeetingsJson)
				.put("otherMeetings", otherMeetingsJson)
				.toString();
	}

}
