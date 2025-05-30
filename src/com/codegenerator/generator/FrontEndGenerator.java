package com.codegenerator.generator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.codegenerator.connection.JDBCManager;
import com.codegenerator.util.Column;
import com.codegenerator.util.FileManager;
import com.codegenerator.util.PropertiesReading;
import com.codegenerator.util.Table;
import com.codegenerator.util.TextUtil;

public class FrontEndGenerator implements IFrontEndGenerator {

	Set<Object[]> tables;
	JDBCManager jdbcManager;
	String packageName;
	String workspace;
	String projectName;
	String packagePath = "";
	String resourcesPath = "";
	String databaseName;
	String server = "";

	boolean addOAuth2;
	
	private int processProgress = 0;
	
	public FrontEndGenerator(String server, String databaseName, Set<Object[]> tables, JDBCManager jdbcManager,
			String workspace, String projectName, String packageName, boolean addOAuth2) {
		this.databaseName = databaseName;
		this.tables = tables;
		this.jdbcManager = jdbcManager;
		this.packageName = packageName;
		this.workspace = workspace;
		this.projectName = projectName;
		this.packagePath = workspace + "\\" + projectName + "\\src\\app";
		this.resourcesPath = workspace + "\\" + projectName + "\\src\\assets";
		this.server = server;
		this.addOAuth2 = addOAuth2;
	}

	@Override
	public Boolean generate() {

		setProcessProgress(0);
		printLog("Generando...");
		printLog("Creando Directorio para frontend..");
		FileManager.createFolder(workspace, projectName);
		setProcessProgress(30);
		FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/FrontEnd/[projectName]",
				workspace + "\\" + projectName, true);

		FileManager.replaceTextInFilesFolder(workspace + "\\" + projectName, "[projectName]", projectName);
		
		int availableTime = 50;
		int i=30;
		for (Object[] table : tables) {
			i+=(availableTime/(tables.size()));
			setProcessProgress(i);
			String tableName = (String) table[0];
			printLog("Obteniendo columnas de la tabla " +tableName+"");
			
			List<Column> columns = jdbcManager.getColumnsByTable(databaseName, tableName);
			Table tbl = new Table();
			tbl.setName(tableName);
			tbl.setColumns(columns);
			printLog("Generando servicio de la tabla " +tableName+"");
			generateService(tableName);
			printLog("Generando componente de la tabla " +tableName+"");
			generateComponent(tableName, columns);
			
		}
		setProcessProgress(80);
		printLog("Aplicando configuración");
		configurar();
		setProcessProgress(90);
		printLog("Front-End generado exitosamente!");
		setProcessProgress(100);
		return true;
	}

	@Override
	public Boolean generateModel(String tableName, List<Column> columns) {

		return true;
	}

	@Override
	public Boolean generateService(String tableName) {

		String pathService = packagePath + "\\services\\" + TextUtil.convertToSnakeCase(tableName) + ".service.ts";

		try {

			FileManager.copyDir(
					PropertiesReading.folder_codegenerator_util + "/FrontEnd/service/[tableName].service.ts",
					pathService, false);

			FileManager.replaceTextInFile(pathService, "[tableName]Service",
					TextUtil.capitalizeText(TextUtil.convertToCamelCase(tableName)) + "Service");

			FileManager.replaceTextInFile(pathService, "[tableName]", TextUtil.convertToCamelCase(tableName));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	@Override
	public Boolean generateComponent(String tableName, List<Column> columns) {

		FileManager.createFolder(packagePath + "\\views\\", TextUtil.convertToSnakeCase(tableName));

		String componentFolder = packagePath + "\\views\\" + TextUtil.convertToSnakeCase(tableName);

		/*
		 * COMPONENT
		 */

		String componentPath = componentFolder + "\\" + TextUtil.convertToSnakeCase(tableName) + ".component.ts";

		FileManager.copyDir(
				PropertiesReading.folder_codegenerator_util + "/FrontEnd/component/[tableName].component.ts",
				componentPath, false);

		FileManager.replaceTextInFile(componentPath, "CAMEL_CASE_CAP[tableName]",
				TextUtil.capitalizeText(TextUtil.convertToCamelCase(tableName)));

		FileManager.replaceTextInFile(componentPath, "CAMEL_CASE[tableName]", TextUtil.convertToCamelCase(tableName));

		FileManager.replaceTextInFile(componentPath, "SNAKE_CASE[tableName]", TextUtil.convertToSnakeCase(tableName));

		/*
		 * SCSS
		 */
		String scssPath = componentFolder + "\\" + TextUtil.convertToSnakeCase(tableName) + ".component.scss";

		FileManager.copyDir(
				PropertiesReading.folder_codegenerator_util + "/FrontEnd/component/[tableName].component.scss",
				scssPath, false);

		/*
		 * ROUTES
		 */
		String routesPath = componentFolder + "\\" + TextUtil.convertToSnakeCase(tableName) + ".routes.ts";
		FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/FrontEnd/component/[tableName].routes.ts",
				routesPath, false);

		FileManager.replaceTextInFile(routesPath, "CAMEL_CASE_CAP[tableName]",
				TextUtil.capitalizeText(TextUtil.convertToCamelCase(tableName)));

		FileManager.replaceTextInFile(routesPath, "CAMEL_CASE[tableName]", TextUtil.convertToCamelCase(tableName));

		FileManager.replaceTextInFile(routesPath, "SNAKE_CASE[tableName]", TextUtil.convertToSnakeCase(tableName));

		/*
		 * HTML
		 */
		String htmlPath = componentFolder + "\\" + TextUtil.convertToSnakeCase(tableName) + ".component.html";

		FileManager.copyDir(
				PropertiesReading.folder_codegenerator_util + "/FrontEnd/component/[tableName].component.html",
				htmlPath, false);
		FileManager.replaceTextInFile(htmlPath, "CAMEL_CASE_CAP[tableName]",
				TextUtil.capitalizeText(TextUtil.convertToCamelCase(tableName)));

		FileManager.replaceTextInFile(htmlPath, "CAMEL_CASE[tableName]", TextUtil.convertToCamelCase(tableName));

		// crear columnas de la tabla

		String columnsTable = "";
		String fieldRows = "";

		for (Column column : columns) {

			String[] partes = TextUtil.capitalizeText(TextUtil.convertToCamelCase(column.getName())).split("(?=[A-Z])");
			String columnName = Arrays.stream(partes).collect(Collectors.joining(" "));
			columnsTable += "\t\t\t\t\t\t\t\t<th>" + columnName + "</th>\n";
			fieldRows    += "\t\t\t\t\t\t\t\t<td>{{" + TextUtil.convertToCamelCase(tableName) + "."+ TextUtil.convertToCamelCase(column.getName()) + "}}</td>\n";
		}

		FileManager.replaceTextInFile(htmlPath, "[columnsTable]", columnsTable);
		FileManager.replaceTextInFile(htmlPath, "[fields_row]", fieldRows);
		return true;
	}

	public Boolean configurar() {

		String option = "";

		for (Object[] table : tables) {
			String tableName = (String) table[0];
			option += "\t{\n";
			option += "\t\tname: '"+TextUtil.convertToSnakeCase(tableName)+"',\n";
			option += "\t\turl: '/"+TextUtil.convertToSnakeCase(tableName)+"',\n";
			option += "\t\ticonComponent: { name: 'cil-dollar' }\n";
			option += "\t},\n";
		}
		
		String navClass = packagePath + "\\common\\"+ "_nav.ts";
		FileManager.replaceTextInFile(navClass, "//ArrayOptions", option);
		
		String routes = "";
		for (Object[] table : tables) {
			String tableName = (String) table[0];
			routes += "\t\t{\n";
			routes += "\t\t\tpath: '"+TextUtil.convertToSnakeCase(tableName)+"',\n";
			routes += "\t\t\tloadChildren: () => import('./views/"+TextUtil.convertToSnakeCase(tableName)+"/"+TextUtil.convertToSnakeCase(tableName)+".routes').then((m) => m.routes)\n";
			routes += "\t\t},\n";
		}
		
		String routesClass = packagePath + "\\"+ "app.routes.ts";
		
		FileManager.replaceTextInFile(routesClass, "//ArrayRoutes", routes);

		return true;
	}

	public int getProcessProgress() {
		return processProgress;
	}

	public void setProcessProgress(int processProgress) {
		this.processProgress = processProgress;
	}
	
	private String log ="";
	public String printLog(String text) {
		this.log = getDateTime()+ " - "+ text +"\n";
		setLog(log);
		
		return this.log;
	}

	public void setLog(String log) {	
		
		this.log = log;
	}
	public String getLog() {			
		return this.log;
	}
	private String getDateTime() {		
		// Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Format the date and time (optional)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
	}


}