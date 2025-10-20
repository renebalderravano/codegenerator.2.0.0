package com.codegenerator.util;

import java.util.ArrayList;
import java.util.List;

public class FieldNameFormatter {

	// Separa palabras en camelCase y devuelve una lista
	public static List<String> splitCamelCase(String input) {
		List<String> words = new ArrayList<>();
		if (input == null || input.isEmpty())
			return words;

		StringBuilder currentWord = new StringBuilder();
		for (char c : input.toCharArray()) {
			if (Character.isUpperCase(c) && currentWord.length() > 0) {
				words.add(currentWord.toString());
				currentWord = new StringBuilder();
			}
			currentWord.append(c);
		}
		if (currentWord.length() > 0) {
			words.add(currentWord.toString());
		}

		return words;
	}

	// Convierte camelCase a snake_case
	public static String toSnakeCase(String input) {
		return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
	}

	// Convierte camelCase a kebab-case
	public static String toKebabCase(String input) {
		return input.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
	}

	// Convierte camelCase a PascalCase
	public static String toPascalCase(String input) {
		String[] parts = input.split("(?=[A-Z])");
		StringBuilder result = new StringBuilder();
		for (String part : parts) {
			result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase());
		}
		return result.toString();
	}
	
	   public static String toCamelCase(String input) {
	        if (input == null || input.isEmpty()) return input;

	        String[] parts = input.split("[_\\s-]+");
	        StringBuilder camelCase = new StringBuilder();

	        for (int i = 0; i < parts.length; i++) {
	            String part = parts[i].toLowerCase();
	            if (i == 0) {
	                camelCase.append(part);
	            } else {
	                camelCase.append(Character.toUpperCase(part.charAt(0)))
	                         .append(part.substring(1));
	            }
	        }

	        return camelCase.toString();
	    }

	
	public static String splitCamelCaseToString(String input) {
	    return input.replaceAll("([a-z])([A-Z])", "$1 $2");
	}

}
