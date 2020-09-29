package it.polimi.tiw.mi145.riunioniOnline.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

@WebServlet("/PatternSelectionHandler")
@MultipartConfig

public class PatternSelectionHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PatternSelectionHandler() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pattern = null;
		pattern = StringEscapeUtils.escapeJava(request.getParameter("pattern"));
		String path = getServletContext().getContextPath();

		switch (pattern) {
		case "HTML":
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("HTML pattern selected");
			path += "/indexPureHtml.html";
			break;
		case "RIA":
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("RIA pattern selected");
			path += "/index.html";
			break;
		default:
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.getWriter().println("Pattern not valid");
			path += "/patternSelection.html";
			break;
		}
		response.sendRedirect(path);
	}
}
