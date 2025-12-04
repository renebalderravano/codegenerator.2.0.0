package com.codegenerator.util;

public class Column {

	private String name;
	private String dataType;
	private Integer length;
	private Integer numericPrecision;
	private Integer numericScale;
	private Boolean isNullable;
	private Boolean isPrimaryKey;
	private Boolean isForeigKey;
	private Boolean autoIncrement;
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

	public void setIsNullable(Boolean isNullable) {
		this.isNullable = isNullable;
	}



	public Boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}

	public void setIsPrimaryKey(Boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public Boolean getIsForeigKey() {
		return isForeigKey;
	}

	public void setIsForeignKey(Boolean isForeigKey) {
		this.isForeigKey = isForeigKey;
	}

	public Boolean getAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(Boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getTableReference() {
		return tableReference;
	}

	public void setTableReference(String tableReference) {
		this.tableReference = tableReference;
	}

}
