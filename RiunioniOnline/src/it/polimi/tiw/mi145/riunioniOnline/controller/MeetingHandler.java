package it.polimi.tiw.mi145.riunioniOnline.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.polimi.tiw.mi145.riunioniOnline.beans.Meeting;
import it.polimi.tiw.mi145.riunioniOnline.beans.User;
import it.polimi.tiw.mi145.riunioniOnline.dao.MeetingDAO;
import it.polimi.tiw.mi145.riunioniOnline.dao.UserDAO;
import it.polimi.tiw.mi145.riunioniOnline.utils.ConnectionHandler;
import it.polimi.tiw.mi145.riunioniOnline.utils.CookieHandler;
import it.polimi.tiw.mi145.riunioniOnline.utils.DateHandler;

@WebServlet("/MeetingHandler")
@MultipartConfig
public class MeetingHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public MeetingHandler() {
		super();
	}

	public void init() throws ServletException {
		ServletContext context = getServletContext();
		try {

			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer id;
		try {
			id = CookieHandler.getUserIdByCookie(request, connection);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			e.printStackTrace();
			return;
		}

		if (id == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Session expired, log in again");
			return;
		}

		User person = null;
		List<Meeting> ownMeetings = null;
		List<Meeting> otherMeetings = null;
		List<String[]> idsAndNames = null;

		try {
			UserDAO userDAO = new UserDAO(connection);
			person = userDAO.getUserById(Integer.valueOf(id));
			ownMeetings = new ArrayList<>();

			MeetingDAO meetingDAO = new MeetingDAO(connection);

			for (int _id : person.getOwnMeetings()) {
				ownMeetings.add(meetingDAO.getMeetingbyId(_id));
			}

			otherMeetings = new ArrayList<>();

			for (int _id : person.getOtherMeetings()) {
				otherMeetings.add(meetingDAO.getMeetingbyId(_id));
			}

			idsAndNames = userDAO.getAllIdsAndNames();

		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			e.printStackTrace();
			return;
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userName", person.getUserName());

		JSONArray ownMeetingsJson = new JSONArray();
		JSONObject ownMeetingJson = null;
		for (Meeting meeting : ownMeetings) {
			ownMeetingJson = new JSONObject();
			ownMeetingJson.put("name", meeting.getName());
			ownMeetingJson.put("date", DateHandler.fromUtilToString(meeting.getDate()));
			ownMeetingJson.put("expirationDate", DateHandler.fromUtilToString(meeting.getExpirationDate()));
			ownMeetingsJson.put(ownMeetingJson);
		}
		jsonObject.put("ownMeetings", ownMeetingsJson);

		JSONArray otherMeetingsJson = new JSONArray();
		JSONObject otherMeetingJson = null;
		for (Meeting meeting : otherMeetings) {
			otherMeetingJson = new JSONObject();
			otherMeetingJson.put("name", meeting.getName());
			otherMeetingJson.put("date", DateHandler.fromUtilToString(meeting.getDate()));
			otherMeetingJson.put("expirationDate", DateHandler.fromUtilToString(meeting.getExpirationDate()));
			otherMeetingsJson.put(otherMeetingJson);
		}
		jsonObject.put("otherMeetings", otherMeetingsJson);

		JSONArray idsAndNamesJson = new JSONArray();
		JSONObject idAndNameJson = null;
		for (String[] idAndName : idsAndNames) {
			if (!idAndName[0].equals(String.valueOf(id))) {
				idAndNameJson = new JSONObject();
				idAndNameJson.put("id", idAndName[0]);
				idAndNameJson.put("username", idAndName[1]);
				idsAndNamesJson.put(idAndNameJson);
			}
		}
		jsonObject.put("idsAndNames", idsAndNamesJson);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(jsonObject.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Integer id;
		try {
			id = CookieHandler.getUserIdByCookie(request, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}

		if (id == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Session expired, log in again");
			return;
		}

		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}

		String jsonString = sb.toString();
		System.out.println(jsonString);

		if (jsonString == null || jsonString.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Nothing has been recived");
			return;
		}

		JSONObject json = null;
		String name = null;
		Date date = null;
		Date expirationDate = null;
		List<Integer> participants = null;

		try {
			json = new JSONObject(jsonString);
			name = json.getString("name");
			date = DateHandler.fromStringToUtil(json.getString("date"));
			expirationDate = DateHandler.fromStringToUtil(json.getString("expirationDate"));
			participants = new ArrayList<>();

			JSONArray participantsArray = json.getJSONArray("participants");

			for (int i = 0; i < participantsArray.length(); i++) {
				participants.add(participantsArray.getInt(i));
			}
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad request");
			return;
		}

		if (expirationDate.before(date) || expirationDate.before(new Date())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Impossible expiration date");
			return;
		}

		MeetingDAO meetingDAO = new MeetingDAO(connection);

		try {
			meetingDAO.addMeating(name, date, expirationDate, participants, id);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
