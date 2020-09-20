package it.polimi.tiw.mi145.riunioniOnline.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateHandler {
	public static java.sql.Date fromUtilToSql(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.util.Date fromStringToUtil(String dateInString) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));

		return formatter.parse(dateInString);
	}
}
