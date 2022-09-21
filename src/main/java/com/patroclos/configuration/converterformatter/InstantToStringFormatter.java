package com.patroclos.configuration.converterformatter;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import com.patroclos.utils.DateUtil;

@Component
public class InstantToStringFormatter implements Formatter<Instant>{

	@Override
	public String print(Instant instant, Locale locale) {
		return DateTimeFormatter.ofPattern(DateUtil.UI_DATE_TIME_FORMAT)
				.withLocale(locale)
				.withZone(ZoneId.systemDefault()).
				format(instant);
	}

	public String print(Instant instant) {
		return print(instant, Locale.getDefault());
	}
	
	@Override
	public Instant parse(String text, Locale locale) throws ParseException {
		String temp =  DateUtil.convertUiDateTimeToDbDateTimeFormat(text);
		temp = temp + ":00.00Z";
		return Instant.parse(temp);
	}

}