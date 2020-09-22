package it.polimi.tiw.mi145.riunioniOnline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IntersectionDAO {
	private Connection connection;

	public IntersectionDAO(Connection connection) {
		this.connection = connection;
	}

	public void addIntersection(int userId, int meetingId) throws SQLException {
		String query = "INSERT into intersection (user, meeting) VALUES (?, ?)";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, meetingId);
			preparedStatement.executeUpdate();
		}
	}

	public List<Integer> getAllMeetingsIdByUserId(int userId) throws SQLException {
		String query = "SELECT meeting from intersection WHERE user = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return new ArrayList<>();
				else {
					ArrayList<Integer> toReturn = new ArrayList<>();
					while (result.next()) {
						toReturn.add(result.getInt("meeting"));
					}
					return toReturn;
				}
			}
		}
	}

	public List<Integer> getAllUserIdByMeetingId(int meetingId) throws SQLException {
		String query = "SELECT user from intersection WHERE meeting = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, meetingId);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					ArrayList<Integer> toReturn = new ArrayList<>();
					while (result.next()) {
						toReturn.add(result.getInt("user"));
					}
					return toReturn;
				}
			}
		}
	}
	
	public void removeIntersectionByMeetingId(int meetingId) throws SQLException {
		String query = "DELETE from intersection WHERE meeting = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, meetingId);
			preparedStatement.executeUpdate();
		}
	}
}
