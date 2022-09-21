package com.patroclos.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

	public static final String UI_DATE_FORMAT = "dd-MM-yyyy";
	public static final String UI_DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";
	public static final String DATABASE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
	
	public static String getUICurrentDateTime() {
		LocalDateTime now = LocalDateTime.now();
		return convertDateToString(now, UI_DATE_TIME_FORMAT);
	}

	public static String convertDbDateToUiDateFormat(Object dbDate) {
		if (dbDate instanceof Instant) {
			return DateTimeFormatter.ofPattern(DateUtil.UI_DATE_TIME_FORMAT)
					.withZone(ZoneId.systemDefault()).format((Instant)dbDate);
		}
		else
			return convertDbDateToUiDateFormat(dbDate.toString());
	}

	public static String convertDbDateToUiDateFormat(String dbDate) {
		String uiDate = null;
		String dateFormat = UI_DATE_TIME_FORMAT;
		String dbDateFormat = DATABASE_DATE_TIME_FORMAT;
		if (dbDate.toString().length() > 16) {
			dbDate = dbDate.toString().substring(0, 16);
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dbDateFormat);
		LocalDateTime localDate = LocalDateTime.parse(dbDate, formatter);
		formatter = DateTimeFormatter.ofPattern(dateFormat);
		uiDate = formatter.format(localDate);
		return uiDate;
	}

	public static String convertUiDateToDbDateFormat(String uiDate) {
		String dbDate = null;
		String dateFormat = UI_DATE_FORMAT;
		String dbDateFormat = DATABASE_DATE_TIME_FORMAT;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		LocalDate localDate = LocalDate.parse(uiDate, formatter);
		//formatter = DateTimeFormatter.ofPattern(dbDateFormat);
		dbDate = localDate.toString();//formatter.format(localDate);
		return dbDate;
	}

	public static String convertUiDateTimeToDbDateTimeFormat(String uiDateTime) {
		String dbDateTime = null;
		String dateTimeFormat = UI_DATE_TIME_FORMAT;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
		LocalDateTime localDateTime = LocalDateTime.parse(uiDateTime, formatter);
		//formatter = DateTimeFormatter.ofPattern(dbDateFormat);
		dbDateTime = localDateTime.toString();//formatter.format(localDate);
		return dbDateTime;
	}

	public static String convertDateToString(LocalDateTime dateTime, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return formatter.format(dateTime);
	}

	public static Instant convertUiDateTimeStringToInstant(String uiDateTime) {
		DateTimeFormatter f = DateTimeFormatter
				.ofPattern(DateUtil.UI_DATE_TIME_FORMAT)
				.withZone(ZoneId.systemDefault());
		LocalDateTime ldt = LocalDateTime.parse(uiDateTime, f);
		return ZonedDateTime.of(ldt, ZoneId.systemDefault()).toInstant();
	}
}
