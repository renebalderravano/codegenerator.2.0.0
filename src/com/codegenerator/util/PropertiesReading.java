package com.codegenerator.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReading {
	
	public static String folder_codegenerator_util = "c://codegenerator_util";
	
	static Properties prop = new Properties();
	
	static {
        try (InputStream in = new FileInputStream(folder_codegenerator_util+"/application.properties")) {

        	  prop.load(in);

        } catch (IOException io) {
            io.printStackTrace();
        }
	}

	
	public static String getProperty(String propertyName) {
		return (String) prop.get(propertyName);
	}
	
	
}
