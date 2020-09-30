package it.polimi.tiw.mi145.riunioniOnline.controller.pureHTML;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.mi145.riunioniOnline.beans.User;
import it.polimi.tiw.mi145.riunioniOnline.dao.UserDAO;
import it.polimi.tiw.mi145.riunioniOnline.utils.ConnectionHandler;
import it.polimi.tiw.mi145.riunioniOnline.utils.CookieHandler;

@WebServlet("/SignUpPureHTML")
@MultipartConfig
public class SignUpPureHTML extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SignUpPureHTML() {
		super();
		// TODO Auto-generated constructor stub
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = null;
		String password = null;
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.sendRedirect(
					getServletContext().getContextPath() + "/AlertPureHtml?message=Credentials+must+be+not+null&url="
							+ getServletContext().getContextPath() + "/indexPureHTML.html");
			return;
		}
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			user = userDao.addUser(username, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.sendRedirect(getServletContext().getContextPath()
					+ "/AlertPureHtml?message=Internal+server+error,+retry+later&url="
					+ getServletContext().getContextPath() + "/indexPureHTML.html");
			e.printStackTrace();
			return;
		}

		if (user == null) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.sendRedirect(
					getServletContext().getContextPath() + "/AlertPureHtml?message=Duplicate+username&url="
							+ getServletContext().getContextPath() + "/indexPureHTML.html");
		} else {
			try {
				Cookie cookie = CookieHandler.getValidCookieByUserId(user.getId(), connection);
				cookie.setMaxAge(24 * 60 * 60);
				response.addCookie(cookie);
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.sendRedirect(getServletContext().getContextPath() + "/homePagePureHTML");
			} catch (SQLException e) {
				;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.sendRedirect(getServletContext().getContextPath()
						+ "/AlertPureHtml?message=Internal+server+error,+retry+later&url="
						+ getServletContext().getContextPath() + "/indexPureHTML.html");
				e.printStackTrace();
				return;
			}
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
