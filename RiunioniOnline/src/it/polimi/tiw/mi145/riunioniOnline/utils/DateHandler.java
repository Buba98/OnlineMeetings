package it.polimi.tiw.mi145.riunioniOnline.utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateHandler {
	public static java.sql.Date fromUtilToSql(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.util.Date fromStringToUtil(String dateInString) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		formatter.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
		return formatter.parse(dateInString);
	}
	
	public static String fromUtilToString(java.util.Date date) {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(date);
	}
	
	public static java.sql.Timestamp fromUtilToTimestamp(java.util.Date date){
		return new java.sql.Timestamp(date.getTime());
	}
	
	public static java.util.Date fromTimestampToUtil(java.sql.Timestamp timestamp){
		return new java.util.Date(timestamp.getTime());
	}
}
