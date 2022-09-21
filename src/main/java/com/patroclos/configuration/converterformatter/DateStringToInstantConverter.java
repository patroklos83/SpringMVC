package com.patroclos.configuration.converterformatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.patroclos.utils.DateUtil;

@Component
public class DateStringToInstantConverter implements Converter<String, Instant> {

	@Override
	public Instant convert(String source) {
		return DateUtil.convertUiDateTimeStringToInstant(source);
	}
}