package com.patroclos.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionUtil {

	/***
	 * Returns all the fields in a class which are encapsulated and have getters and setters
	 * @param datalistType
	 * @return
	 */
	public static List<Field> getFieldList(Class<?> entityClass){			
		Field[] allFields = entityClass.getDeclaredFields();
		return Arrays.stream(allFields)
				.filter(f -> Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers())
						|| Modifier.isPrivate(f.getModifiers()))
				.filter(f -> !Modifier.isFinal( f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
				.collect(Collectors.toList());
	}
	
	public static List<Field> getFieldListOfDTO(Class<?> entityClass){			
		Field[] allFields = entityClass.getDeclaredFields();
	    // Get fields of DTO
		List<Field> filteredFields =  Arrays.stream(allFields)
				.filter(f -> Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers())
						|| Modifier.isPrivate(f.getModifiers()))
				.filter(f -> !Modifier.isFinal( f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
				.collect(Collectors.toList());
	    // Get ID field of DTO superclass
	    Field[] superClassFields = entityClass.getSuperclass().getDeclaredFields();
	    List<Field> filteredSuperclassFields =  Arrays.stream(superClassFields)
				.filter(f -> Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers())
						|| Modifier.isPrivate(f.getModifiers()))
				.filter(f -> !Modifier.isFinal( f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
				.filter(f -> f.getName().equals("id"))
				.collect(Collectors.toList());
	    
	    filteredSuperclassFields.addAll(filteredFields);
	    
	    return filteredSuperclassFields;
	}

}
