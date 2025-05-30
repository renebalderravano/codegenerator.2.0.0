package com.codegenerator.util;

public class TextUtil {
	
	
	public static String convertToCamelCase(String snakeCase) {
        StringBuilder camelCase = new StringBuilder();
        boolean toUpperCase = false;

        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                toUpperCase = true; // La siguiente letra será mayúscula
            } else {
                if (toUpperCase) {
                    camelCase.append(Character.toUpperCase(c));
                    toUpperCase = false;
                } else {
                    camelCase.append(c);
                }
            }
        }

        return camelCase.toString();
    }
	
	
	
	public static String convertToSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase; // Manejo de casos nulos o vacíos
        }
        // Reemplaza las transiciones de mayúsculas con un guion bajo seguido de la letra en minúscula
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
	
	
	
	public static String capitalizeText(String text) {
		text = text.toLowerCase().substring(0, 1).toUpperCase() + text.toLowerCase().substring(1, text.length());
		return text;
	}

}
