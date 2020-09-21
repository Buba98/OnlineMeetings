package it.polimi.tiw.mi145.riunioniOnline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.mi145.riunioniOnline.beans.Meeting;
import it.polimi.tiw.mi145.riunioniOnline.utils.DateHandler;

public class MeetingDAO {
	private Connection connection;

	public MeetingDAO(Connection connection) {
		this.connection = connection;
	}

	public void removeMeeting(Integer id) {

	}

	public void addMeating(String name, Date date, Date expirationDate, List<Integer> participants, Integer owner)
			throws SQLException {
		IntersectionDAO intersectionDAO = new IntersectionDAO(connection);
		String query = "INSERT into meeting (datestart, dateend, owner, name) VALUES(?, ?, ?, ?)";

		try (PreparedStatement preparedStatement = connection.prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setDate(1, DateHandler.fromUtilToSql(date));
			preparedStatement.setDate(2, DateHandler.fromUtilToSql(expirationDate));
			preparedStatement.setInt(3, owner);
			preparedStatement.setString(4, name);
			preparedStatement.executeUpdate();
			try (ResultSet result = preparedStatement.getGeneratedKeys()) {
				Integer meetingId = result.getInt("idmeeting");

				intersectionDAO.addIntersection(owner, meetingId);

				for (Integer participant : participants) {
					intersectionDAO.addIntersection(participant, meetingId);
				}
			}
		}
	}

	public Meeting getMeetingbyId(Integer id) throws SQLException {
		IntersectionDAO intersectionDAO = new IntersectionDAO(connection);
		String query = "SELECT * from meeting WHERE id = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, id);
			try (ResultSet result = connection.prepareStatement(query).executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					Date date = result.getDate("datestart");
					Date expirationDate = result.getDate("dateend");
					Integer owner = result.getInt("owner");
					String name = result.getString("name");

					List<Integer> participants = new ArrayList<>();

					for (Integer participant : intersectionDAO.getAllUserIdByMeetingId(id)) {
						if (participant != owner) {
							participants.add(participant);
						}
					}

					return new Meeting(name, date, expirationDate, participants, id, owner);
				}
			}
		}
	}

}
