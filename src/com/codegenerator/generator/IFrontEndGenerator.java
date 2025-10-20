package com.codegenerator.generator;

import java.util.List;

import com.codegenerator.util.Column;
import com.codegenerator.util.Table;

public interface IFrontEndGenerator {
	
	public Boolean generate();
	public Boolean generateModel(String tableName, List<Column> columns);
	public Boolean generateService(String tableName);
	public Boolean generateComponent(String tableName, List<Column> columns);
	public Boolean generateComponent(Table table);
	
}
