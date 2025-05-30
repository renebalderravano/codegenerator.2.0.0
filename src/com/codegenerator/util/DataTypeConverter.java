package com.codegenerator.util;

import java.util.HashMap;
import java.util.Map;

public class DataTypeConverter {
	
	
	public static Map<String, String> mysqlToJava = new HashMap<String, String>(){{
		put("bit", "boolean");
		put("tinyint", "Integer");
		put("smallint", "Integer");
		put("mediumint", "Integer");		
		put("int", "Integer");
		put("integer", "Integer");
		put("bigint", "java.math.BigInteger");
		put("float", "float");
		put("double", "double");
		put("decimal", "java.math.BigDecimal");		
		put("date", "java.sql.Date");
		put("datetime", "java.time.LocalDateTime");
		put("timestamp", "java.sql.Timestamp");
		put("char", "String");
		put("varchar", "String");
		put("text", "String");
		put("tinytext", "String");
		put("mediumtext", "String");
		put("longtext", "String");
		put("json", "String");
		put("binary", "byte[]");
		put("varbinary", "byte[]");
		put("blob", "byte[]");
		put("tinyblob", "byte[]");
		put("mediumblob", "byte[]");
		put("longblob", "byte[]");
		
	}};
	
	
	public static Map<String, String> sqlserverToJava = new HashMap<String, String>(){{
		put("bit", "boolean");
		put("tinyint", "short");
		put("smallint", "short");		
		put("int", "Integer");
		put("real","float");
		put("bigint","long");	
		put("float","double");	
		put("nchar","String");	
		put("nvarchar","String");	
		put("binary","byte[]");
		put("varbinary","byte[]");
		put("image","byte[]");
		put("nvarchar","String");
		put("varbinary","byte[]");	
		put("uniqueidentifier","String");	
		put("char","String");	
		put("varchar","String");
		put("date","java.sql.date");
		put("numeric","java.math.BigDecimal");
		put("decimal","java.math.BigDecimal");	
		put("money","java.math.BigDecimal");	
		put("smallmoney","java.math.BigDecimal");	
		put("smalldatetime","java.sql.timestamp");	
		put("datetime","java.sql.timestamp");	
		put("datetime2","java.sql.timestamp");

		
	}};
	
	public DataTypeConverter() {

		
	
		
		
	}
	

}
