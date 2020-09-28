package it.polimi.tiw.mi145.riunioniOnline.beans;

import java.util.Date;

public class Session {
	
	private final String idSession;
	private final int user;
	private final Date date;
	
	public Session(String idSession, int user, Date date) {
		this.idSession = idSession;
		this.user = user;
		this.date = date;
	}

	public String getIdSession() {
		return idSession;
	}

	public int getUser() {
		return user;
	}

	public Date getDate() {
		return date;
	}
	
	

}
