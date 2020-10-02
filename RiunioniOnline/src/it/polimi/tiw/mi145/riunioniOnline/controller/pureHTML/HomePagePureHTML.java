package it.polimi.tiw.mi145.riunioniOnline.controller.pureHTML;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.mi145.riunioniOnline.beans.User;
import it.polimi.tiw.mi145.riunioniOnline.dao.MeetingDAO;
import it.polimi.tiw.mi145.riunioniOnline.dao.UserDAO;
import it.polimi.tiw.mi145.riunioniOnline.utils.ConnectionHandler;
import it.polimi.tiw.mi145.riunioniOnline.utils.CookieHandler;
import it.polimi.tiw.mi145.riunioniOnline.utils.DateHandler;

@WebServlet("/HomePagePureHTML")
public class HomePagePureHTML extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		try {

			String driver = servletContext.getInitParameter("dbDriver");
			String url = servletContext.getInitParameter("dbUrl");
			String user = servletContext.getInitParameter("dbUser");
			String password = servletContext.getInitParameter("dbPassword");
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
			response.sendRedirect(getServletContext().getContextPath()
					+ "/AlertPureHtml?message=Internal+server+error,+retry+later&url="
					+ getServletContext().getContextPath() + "/indexPureHtml.html");
			e.printStackTrace();
			return;
		}

		if (id == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.sendRedirect(
					getServletContext().getContextPath() + "/AlertPureHtml?message=Session+expired,+log+in+again&url="
							+ getServletContext().getContextPath() + "/indexPureHtml.html");
			return;
		}

		User user = null;
		List<Map<String, Object>> ownMeetings = null;
		List<Map<String, Object>> otherMeetings = null;
		List<Map<String, Object>> idsAndNames = new ArrayList<>();

		try {
			UserDAO userDAO = new UserDAO(connection);
			user = userDAO.getUserById(Integer.valueOf(id));
			ownMeetings = new ArrayList<>();

			MeetingDAO meetingDAO = new MeetingDAO(connection);

			for (int _id : user.getOwnMeetings()) {
				ownMeetings.add(meetingDAO.getMeetingbyId(_id).getMap());
			}

			otherMeetings = new ArrayList<>();

			for (int _id : user.getOtherMeetings()) {
				otherMeetings.add(meetingDAO.getMeetingbyId(_id).getMap());
			}

			Map<String, Object> map;

			for (String[] idAndName : userDAO.getAllIdsAndNames()) {

				if (Integer.valueOf(idAndName[0]) != id) {

					map = new HashMap<String, Object>();

					map.put("id", idAndName[0]);
					map.put("name", StringEscapeUtils.unescapeJava(idAndName[1]));

					idsAndNames.add(map);
				}
			}

		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.sendRedirect(getServletContext().getContextPath()
					+ "/AlertPureHtml?message=Internal+server+error,+retry+later&url="
					+ getServletContext().getContextPath() + "/indexPureHtml.html");
			e.printStackTrace();
			return;
		}

		String path = "/WEB-INF/HomePagePureHTML.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("ownMeetings", ownMeetings);
		map.put("otherMeetings", otherMeetings);
		map.put("idsAndNames", idsAndNames);
		map.put("username", user.getUserName());

		ctx.setVariables(map);

		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer id;
		try {
			id = CookieHandler.getUserIdByCookie(request, connection);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.sendRedirect(getServletContext().getContextPath()
					+ "/AlertPureHtml?message=Internal+server+error,+retry+later&url="
					+ getServletContext().getContextPath() + "/indexPureHtml.html");
			e.printStackTrace();
			return;
		}

		if (id == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.sendRedirect(
					getServletContext().getContextPath() + "/AlertPureHtml?message=Session+expired,+log+in+again&url="
							+ getServletContext().getContextPath() + "/indexPureHtml.html");
			return;
		}

		String name = null;
		String date = null;
		String hoursAndMinutes = null;
		String expirationHours = null;
		String expirationMinutes = null;
		String maxParticipants = null;
		String[] participants = null;
		name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		date = StringEscapeUtils.escapeJava(request.getParameter("date"));
		hoursAndMinutes = StringEscapeUtils.escapeJava(request.getParameter("hoursAndMinutes"));
		expirationHours = StringEscapeUtils.escapeJava(request.getParameter("expirationHours"));
		expirationMinutes = StringEscapeUtils.escapeJava(request.getParameter("expirationMinutes"));
		maxParticipants = StringEscapeUtils.escapeJava(request.getParameter("maxParticipants"));
		participants = request.getParameterValues("checkbox[]");

		if (name == null || name.isEmpty() || date == null || date.isEmpty() || hoursAndMinutes == null
				|| hoursAndMinutes.isEmpty() || expirationHours == null || expirationHours.isEmpty()
				|| expirationMinutes == null || expirationMinutes.isEmpty() || maxParticipants == null
				|| maxParticipants.isEmpty() || participants == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.sendRedirect(
					getServletContext().getContextPath() + "/AlertPureHtml?message=Form+fields+must+be+not+null&url="
							+ getServletContext().getContextPath() + "/HomePagePureHTML");
			return;
		}

		Date _startDate = null;
		Date _endDate = null;
		List<Integer> _participants = null;

		try {
			_startDate = DateHandler.fromStringToUtil(date + " " + hoursAndMinutes + ":00");
			_endDate = new Date(_startDate.getTime() + Long.valueOf(expirationHours) * 60 * 60 * 1000
					+ Long.valueOf(expirationMinutes) * 60 * 1000);

			if (!(_endDate.after(_startDate) && _endDate.after(new Date()))) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.sendRedirect(getServletContext().getContextPath()
						+ "/AlertPureHtml?message=Date+or/and+duration+are+not+valid&url="
						+ getServletContext().getContextPath() + "/HomePagePureHTML");
				return;
			}

			if (Integer.valueOf(maxParticipants) < participants.length) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.sendRedirect(getServletContext().getContextPath()
						+ "/AlertPureHtml?message=Participants+can't+be+more+than+max+participants&url="
						+ getServletContext().getContextPath() + "/HomePagePureHTML");
				return;
			}

			_participants = new ArrayList<>();

			for (String participant : participants) {
				_participants.add(Integer.valueOf(participant));
			}

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.sendRedirect(getServletContext().getContextPath() + "/AlertPureHtml?message=Bad+request&url="
					+ getServletContext().getContextPath() + "/HomePagePureHTML");
			return;
		}

		MeetingDAO meetingDAO = new MeetingDAO(connection);

		try {
			meetingDAO.addMeating(name, _startDate, _endDate, _participants, id);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.sendRedirect(getServletContext().getContextPath()
					+ "/AlertPureHtml?message=Internal+server+error,+retry+later&url="
					+ getServletContext().getContextPath() + "/indexPureHtml.html");
			return;
		}

		response.setStatus(HttpServletResponse.SC_OK);

		response.sendRedirect(getServletContext().getContextPath()
				+ "/HomePagePureHTML");
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
