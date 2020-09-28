package it.polimi.tiw.mi145.riunioniOnline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import it.polimi.tiw.mi145.riunioniOnline.beans.Session;
import it.polimi.tiw.mi145.riunioniOnline.utils.DateHandler;

public class SessionDAO {
	private Connection connection;

	public SessionDAO(Connection connection) {
		this.connection = connection;
	}

	public String addSession(int userId) throws SQLException {
		String query = "INSERT into session (idsession, user, date) VALUES (?, ?, ?)";

		String id = null;

		while (id == null) {
			id = UUID.randomUUID().toString();
			if (getSessionById(id.toString()) != null)
				id = null;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, id);
			preparedStatement.setInt(2, userId);
			preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			preparedStatement.executeUpdate();
		}

		return id;
	}

	public Session getSessionById(String uuid) throws SQLException {
		String query = "SELECT * from intersection WHERE idsession = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, uuid);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					return new Session(uuid, result.getInt("user"),
							DateHandler.fromTimestampToUtil(result.getTimestamp("date")));
				}
			}
		}
	}

	public void removeSessionByUserId(int userId) throws SQLException {
		String query = "DELETE from session WHERE user = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, userId);
			preparedStatement.executeUpdate();
		}
	}

	public void removeSessionById(String id) throws SQLException {
		String query = "DELETE from session WHERE idsession = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, id);
			preparedStatement.executeUpdate();
		}
	}
}