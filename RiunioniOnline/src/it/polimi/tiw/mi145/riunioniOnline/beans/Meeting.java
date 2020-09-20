package it.polimi.tiw.mi145.riunioniOnline.beans;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class Meeting {
	private final Integer id;
	private final String name;
	private final Date date;
	private final Date expirationDate;
	private final List<Integer> participants;
	private final Integer owner;

	public Meeting(String name, Date date, Date expirationDate, List<Integer> participants, Integer id, Integer owner) {
		this.name = name;
		this.date = date;
		this.expirationDate = expirationDate;
		this.participants = participants;
		this.id = id;
		this.owner = owner;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public List<Integer> getParticipants() {
		return participants;
	}

	public Integer getOwner() {
		return owner;
	}

	public String toJson() {

		JSONObject partecipantsJson = new JSONObject();
		int i = 0;
		for (Integer id : participants) {
			partecipantsJson.put(String.valueOf(i), id.toString());
			i++;
		}

		return new JSONObject()
				.put("id", id.toString())
				.put("name", name)
				.put("date", date.toString())
				.put("expirationDate", expirationDate.toString())
				.put("partecipants", partecipantsJson.toString())
				.put("owner", owner.toString())
				.toString();
	}
}
