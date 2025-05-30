package com.codegenerator.generator;

import java.util.List;

import com.codegenerator.util.Column;

public interface IFrontEndGenerator {
	
	public Boolean generate();
	public Boolean generateModel(String tableName, List<Column> columns);
	public Boolean generateService(String tableName);
	public Boolean generateComponent(String tableName, List<Column> columns);
	
}
