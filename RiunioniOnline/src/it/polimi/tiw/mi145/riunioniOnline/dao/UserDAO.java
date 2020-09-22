package it.polimi.tiw.mi145.riunioniOnline.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.mi145.riunioniOnline.beans.Meeting;
import it.polimi.tiw.mi145.riunioniOnline.beans.User;

public class UserDAO {
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	private User fromResult(ResultSet result) throws SQLException {
		IntersectionDAO intersectionDAO = new IntersectionDAO(connection);
		MeetingDAO meetingDAO = new MeetingDAO(connection);

		int id = result.getInt("iduser");
		String username = result.getString("username");
		String password = result.getString("password");

		List<Integer> meetings = intersectionDAO.getAllMeetingsIdByUserId(id);

		List<Integer> ownMeetings = new ArrayList<>();

		List<Integer> otherMeetings = new ArrayList<>();

		Meeting temp;

		for (Integer meeting : meetings) {
			temp = meetingDAO.getMeetingbyId(meeting);

			if (temp.getExpirationDate().before(new Date())) {
				meetingDAO.removeMeeting(meeting);
				continue;
			}

			if (temp.getOwner() == id) {
				ownMeetings.add(meeting);
			} else {
				otherMeetings.add(meeting);
			}
		}

		return new User(id, username, password, ownMeetings, otherMeetings);
	}

	public User checkCredentials(String userName, String password) throws SQLException {
		String query = "SELECT * from user WHERE username = ? AND password = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, userName);
			preparedStatement.setString(2, password);
			try (ResultSet result = preparedStatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					return fromResult(result);
				}
			}
		}
	}

	public User addUser(String username, String password) throws SQLException {
		if (getUserByUsername(username) == null) {
			String query = "INSERT into user (username, password) VALUES(?, ?)";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password);
				preparedStatement.executeUpdate();

				return getUserByUsername(username);
			}
		} else
			return null;
	}

	public List<String[]> getAllIdsAndNames() throws SQLException {
		String query = "SELECT iduser, username from user";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					List<String[]> toReturn = new ArrayList<>();
					while (result.next()) {
						toReturn.add(
								new String[] { String.valueOf(result.getInt("iduser")), result.getString("username") });
					}
					return toReturn;
				}
			}
		}
	}

	public User getUserById(Integer id) throws SQLException {
		String query = "SELECT * from user WHERE iduser = ?";

		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery()) {

				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					return fromResult(result);
				}
			}
		}
	}

	public User getUserByUsername(String username) throws SQLException {
		String query = "SELECT * from user WHERE username = ?";

		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					return fromResult(result);
				}
			}

		}
	}

}
