package com.patroclos.configuration.converterformatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ObjectToByteConverter implements Converter<Object, byte[]> {

	@Override
	public byte[] convert(Object source) {
		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
			ois.writeObject(source);
			return boas.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

}
