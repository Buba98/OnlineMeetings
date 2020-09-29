package it.polimi.tiw.mi145.riunioniOnline.controller.pureHTML;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.mi145.riunioniOnline.beans.Meeting;
import it.polimi.tiw.mi145.riunioniOnline.beans.User;
import it.polimi.tiw.mi145.riunioniOnline.dao.MeetingDAO;
import it.polimi.tiw.mi145.riunioniOnline.dao.UserDAO;
import it.polimi.tiw.mi145.riunioniOnline.utils.ConnectionHandler;
import it.polimi.tiw.mi145.riunioniOnline.utils.CookieHandler;

@WebServlet("/HomePagePureHTML")
public class HomePagePureHTML extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HomePagePureHTML() {
		super();
		// TODO Auto-generated constructor stub
	}

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
			response.getWriter().println("Internal server error, retry later");
			e.printStackTrace();
			return;
		}

		if (id == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Session expired, log in again");
			return;
		}

		User user = null;
		List<Meeting> ownMeetings = null;
		List<Meeting> otherMeetings = null;
		List<Map<String, Object>> idsAndNames = new ArrayList<>();

		try {
			UserDAO userDAO = new UserDAO(connection);
			user = userDAO.getUserById(Integer.valueOf(id));
			ownMeetings = new ArrayList<>();

			MeetingDAO meetingDAO = new MeetingDAO(connection);

			for (int _id : user.getOwnMeetings()) {
				ownMeetings.add(meetingDAO.getMeetingbyId(_id));
			}

			otherMeetings = new ArrayList<>();

			for (int _id : user.getOtherMeetings()) {
				otherMeetings.add(meetingDAO.getMeetingbyId(_id));
			}

			Map<String, Object> map;

			for (String[] idAndName : userDAO.getAllIdsAndNames()) {

				map = new HashMap<String, Object>();

				map.put("id", idAndName[0]);
				map.put("name", idAndName[1]);

				idsAndNames.add(map);
			}

		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
