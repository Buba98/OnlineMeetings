package it.polimi.tiw.mi145.riunioniOnline.controller.pureHTML;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.mi145.riunioniOnline.utils.CookieHandler;

@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
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
		try {
			CookieHandler.removeCookie(request, response, connection);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.sendRedirect(getServletContext().getContextPath() + "/AlertPureHtml?message=Generic+Error&url="
					+ getServletContext().getContextPath() + "/AlertPureHtml");
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("HTML pattern selected");
		response.sendRedirect(getServletContext().getContextPath() + "/indexPureHtml.html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
