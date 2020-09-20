package it.polimi.tiw.mi145.riunioniOnline.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.polimi.tiw.mi145.riunioniOnline.beans.Meeting;
import it.polimi.tiw.mi145.riunioniOnline.beans.Person;
import it.polimi.tiw.mi145.riunioniOnline.dao.MeetingDAO;
import it.polimi.tiw.mi145.riunioniOnline.dao.PersonDAO;
import it.polimi.tiw.mi145.riunioniOnline.utils.ConnectionHandler;
import it.polimi.tiw.mi145.riunioniOnline.utils.DateHandler;
import it.polimi.tiw.mi145.riunioniOnline.utils.StringValidation;

@WebServlet("/MeetingHandler")
@MultipartConfig
public class MeetingHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public MeetingHandler() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = null;
		String cookies = request.getHeader("Cookie");
		String[] rawCookieParams = cookies.split(";");
		for (String cookie : rawCookieParams) {
			if (cookie.split("=")[0].compareTo("person_id") == 0) {
				id = cookie.split("=")[1];
			}
		}

		if (id == null || !StringValidation.isIntValid(id)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Session expired, log in again");
			return;
		}

		Person person = null;
		List<Meeting> ownMeetings = null;
		List<Meeting> otherMeetings = null;
		List<String[]> idsAndNames = null;

		try {
			PersonDAO personDAO = new PersonDAO(connection);
			person = personDAO.getPersonById(Integer.valueOf(id));
			ownMeetings = new ArrayList<>();

			MeetingDAO meetingDAO = new MeetingDAO(connection);

			for (int _id : person.getOwnMeetings()) {
				ownMeetings.add(meetingDAO.getMeetingbyId(_id));
			}

			otherMeetings = new ArrayList<>();

			for (int _id : person.getOtherMeetings()) {
				otherMeetings.add(meetingDAO.getMeetingbyId(_id));
			}

			idsAndNames = personDAO.getAllIdsAndNames();

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
			ownMeetingJson.put("date", meeting.getDate().toString());
			ownMeetingJson.put("expirationDate", meeting.getExpirationDate().toString());
			ownMeetingsJson.put(ownMeetingJson);
		}
		jsonObject.put("ownMeetings", ownMeetingsJson);

		JSONArray otherMeetingsJson = new JSONArray();
		JSONObject otherMeetingJson = null;
		for (Meeting meeting : otherMeetings) {
			otherMeetingJson = new JSONObject();
			otherMeetingJson.put("name", meeting.getName());
			otherMeetingJson.put("date", meeting.getDate().toString());
			otherMeetingJson.put("expirationDate", meeting.getExpirationDate().toString());
			otherMeetingsJson.put(otherMeetingJson);
		}
		jsonObject.put("otherMeetings", otherMeetingsJson);

		JSONArray idsAndNamesJson = new JSONArray();
		JSONObject idAndNameJson = null;
		for (String[] idAndName : idsAndNames) {
			idAndNameJson = new JSONObject();
			idAndNameJson.put("id", idAndName[0]);
			idAndNameJson.put("userName", idAndName[1]);
			idsAndNamesJson.put(idAndNameJson);
		}
		jsonObject.put("idsAndNames", idsAndNamesJson);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(jsonObject.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String id = null;
		String cookies = request.getHeader("Cookie");
		String[] rawCookieParams = cookies.split(";");
		for (String cookie : rawCookieParams) {
			if (cookie.split("=")[0].compareTo("person_id") == 0) {
				id = cookie.split("=")[1];
			}
		}

		if (id == null || !StringValidation.isIntValid(id)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Session expired, log in again");
			return;
		}

		String jsonString = StringEscapeUtils.escapeJava(request.getParameter("newMeeting"));

		if (jsonString == null || jsonString.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad request");
			return;
		}

		JSONObject json = null;
		String name = null;
		Date date = null;
		Date expirationDate = null;
		List<Integer> participants = null;

		try {
			json = new JSONObject(json);
			name = json.getString("name");
			date = DateHandler.fromStringToUtil(json.getString("date"));
			expirationDate = DateHandler.fromStringToUtil(json.getString("expriationDate"));
			participants = new ArrayList<>();

			JSONArray participantsArray = json.getJSONArray("participants");

			for (int i = 0; i < participantsArray.length(); i++) {
				participants.add(participantsArray.getInt(i));
			}
		} catch (JSONException | ParseException e) {
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
			meetingDAO.addMeating(name, date, expirationDate, participants, Integer.valueOf(id));
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			e.printStackTrace();
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
