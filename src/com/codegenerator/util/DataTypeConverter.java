package com.codegenerator.util;

import java.util.HashMap;
import java.util.Map;

public class DataTypeConverter {

	public static Map<String, String> mysqlToJava = new HashMap<String, String>() {
		{
			put("bit", "Boolean");
			put("tinyint", "Integer");
			put("smallint", "Integer");
			put("mediumint", "Integer");
			put("int", "Integer");
			put("integer", "Integer");
			put("bigint", "java.math.BigInteger");
			put("float", "Float");
			put("double", "Double");
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

		}
	};

	public static Map<String, String> sqlserverToJava = new HashMap<String, String>() {
		{
			put("bit", "Boolean");
			put("tinyint", "short");
			put("smallint", "short");
			put("int", "Integer");
			put("real", "Float");
			put("bigint", "Long");
			put("float", "Double");
			put("nchar", "String");
			put("nvarchar", "String");
			put("binary", "byte[]");
			put("varbinary", "byte[]");
			put("image", "byte[]");
			put("nvarchar", "String");
			put("varbinary", "byte[]");
			put("uniqueidentifier", "String");
			put("char", "String");
			put("varchar", "String");
			put("date", "java.sql.Date");
			put("numeric", "java.math.BigDecimal");
			put("decimal", "java.math.BigDecimal");
			put("money", "java.math.BigDecimal");
			put("smallmoney", "java.math.BigDecimal");
			put("smalldatetime", "java.sql.Timestamp");
			put("datetime", "java.sql.Timestamp");
			put("datetime2", "java.sql.Timestamp");

		}
	};

	HashMap<String, String> sqlServerToTypscript = new HashMap<>() {
		{
			put("int", "number");
			put("bigint", "number");
			put("smallint", "number");
			put("tinyint", "number");
			put("decimal", "number");
			put("numeric", "number");
			put("float", "number");
			put("real", "number");

			put("bit", "boolean");

			put("char", "string");
			put("nchar", "string");
			put("varchar", "string");
			put("nvarchar", "string");
			put("text", "string");
			put("ntext", "string");

			put("date", "string");
			put("datetime", "string");
			put("datetime2", "string");
			put("smalldatetime", "string");
			put("time", "string");
			put("timestamp", "string");

			put("uniqueidentifier", "string");

			put("binary", "Uint8Array");
			put("varbinary", "Uint8Array");
			put("image", "Uint8Array");

		}
	};

	public DataTypeConverter() {

	}

}
