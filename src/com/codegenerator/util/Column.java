package com.codegenerator.util;

public class Column {

	private String name;
	private String dataType;
	private Integer length;
	private Integer numericPrecision;
	private Integer numericScale;
	private boolean isNullable;
	private boolean isPrimaryKey;
	private boolean isForeigKey;
	private boolean autoIncrement;
	private String tableReference;

	public Column() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getNumericPrecision() {
		return numericPrecision;
	}

	public void setNumericPrecision(Integer numericPrecision) {
		this.numericPrecision = numericPrecision;
	}

	public Integer getNumericScale() {
		return numericScale;
	}

	public void setNumericScale(Integer numericScale) {
		this.numericScale = numericScale;
	}

	public boolean getIsNullable() {
		return isNullable;
	}

	public void setIsNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}



	public boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}

	public void setIsPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public boolean getIsForeigKey() {
		return isForeigKey;
	}

	public void setIsForeignKey(boolean isForeigKey) {
		this.isForeigKey = isForeigKey;
	}

	public boolean getAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getTableReference() {
		return tableReference;
	}

	public void setTableReference(String tableReference) {
		this.tableReference = tableReference;
	}

}
