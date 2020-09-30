package it.polimi.tiw.mi145.riunioniOnline.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.tiw.mi145.riunioniOnline.utils.DateHandler;

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

	public Map<String, Object> getMap() {

		long diffMs = expirationDate.getTime() - date.getTime();
		int diffDays = (int) Math.floor(diffMs / 86400000);
		int diffHrs = (int) Math.floor((diffMs % 86400000) / 3600000);
		int diffMins = Math.round(((diffMs % 86400000) % 3600000) / 60000);
		Map<String, Object> toReturn = new HashMap<String, Object>();

		toReturn.put("name", this.name);
		toReturn.put("date", DateHandler.fromUtilToString(date));
		toReturn.put("duration", ((diffDays > 0) ? diffDays + " days " : "")
				+ ((diffHrs > 0) ? diffHrs + " hours " : "") + ((diffMins > 0) ? diffMins + " minutes" : ""));

		return toReturn;

	}
}
