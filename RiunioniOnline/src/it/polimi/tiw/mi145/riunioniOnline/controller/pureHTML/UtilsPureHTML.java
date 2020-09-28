package it.polimi.tiw.mi145.riunioniOnline.controller.pureHTML;

import java.io.IOException;
import java.io.PrintWriter;

public class UtilsPureHTML {
	public static void alertPureHTML(PrintWriter out, String urlDestination, String message) throws IOException {
		out.println("<meta http-equiv='refresh' content='3;URL=" + urlDestination + "'>");// redirects after 3 seconds
		out.println("<p style='color:red;'>" + message + "</p>");
	}
}
