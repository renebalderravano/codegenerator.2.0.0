package com.codegenerator.generator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import com.codegenerator.connection.JDBCManager;
import com.codegenerator.generator.template.TemplatePrimeNG;
import com.codegenerator.util.Column;
import com.codegenerator.util.FieldNameFormatter;
import com.codegenerator.util.FileManager;
import com.codegenerator.util.PropertiesReading;
import com.codegenerator.util.Table;
import com.codegenerator.util.TextUtil;

public class FrontEndGenerator2 implements IFrontEndGenerator{
	
	Set<Object[]> tables;
	JDBCManager jdbcManager;
	String packageName;
	String workspace;
	String projectName;
	String packagePath = "";
	String resourcesPath = "";
	String databaseName;
	String server = "";
	String architecture = "mvc";
	boolean addOAuth2;
	private int processProgress = 0;
	
	public FrontEndGenerator2(String server, String databaseName, Set<Object[]> tables, JDBCManager jdbcManager,
			String workspace, String projectName, String packageName, boolean addOAuth2, String architecture) {
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
		this.architecture = architecture;
	}
	

	@Override
	public Boolean generate() {		
		setProcessProgress(0);
		printLog("Generando...");
		printLog("Creando Directorio para frontend..");
		FileManager.createFolder(workspace, projectName);
		setProcessProgress(30);
		FileManager.copyDir(PropertiesReading.folder_codegenerator_util + (this.architecture.equals("hexagonal") ? "//hexagonal" : "//mvc") +"/FrontEnd/[projectName]",
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
			generateComponent(tbl);
			
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
//		FileManager.createFolder(packagePath + "\\service\\", TextUtil.convertToSnakeCase(tableName ));

		String pathService = packagePath + "\\pages\\service\\" + TextUtil.convertToSnakeCase(tableName) + ".service.ts";
		
		try {

			FileManager.copyDir(
					PropertiesReading.folder_codegenerator_util + "//hexagonal/FrontEnd/service/[tableName].service.ts",
					pathService, false);

			FileManager.replaceTextInFile(pathService, "[tableName]Service",
					FieldNameFormatter.toPascalCase(tableName) + "Service");

			FileManager.replaceTextInFile(pathService, "[tableName]", FieldNameFormatter.toPascalCase(tableName));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	@Override
	public Boolean generateComponent(Table table) {

		String tableName = table.getName();
		FileManager.createFolder(packagePath + "\\pages\\", TextUtil.convertToSnakeCase(tableName ));

		String componentFolder = packagePath + "\\pages\\" + FieldNameFormatter.toSnakeCase(tableName);

		/*
		 * COMPONENT LIST
		 */
		String componentListPath = componentFolder + "\\list";

		FileManager.copyDir(
				PropertiesReading.folder_codegenerator_util + "/hexagonal/FrontEnd/component/list",
				componentListPath, false);
		
		FileManager.renameMultipleFilesInFolder(componentListPath, "[tableName]", TextUtil.convertToSnakeCase(tableName));
		
		componentListPath = componentFolder + "\\list\\" + TextUtil.convertToSnakeCase(tableName) + ".list.ts";

		FileManager.replaceTextInFile(componentListPath, "PASCAL_CASE[tableName]",
				FieldNameFormatter.toPascalCase(tableName));
		 

		FileManager.replaceTextInFile(componentListPath, "CAMEL_CASE[tableName]", FieldNameFormatter.toCamelCase(tableName));

		FileManager.replaceTextInFile(componentListPath, "SNAKE_CASE[tableName]", FieldNameFormatter.toSnakeCase(tableName));
	
		
		componentListPath = componentFolder + "\\list\\" + TextUtil.convertToSnakeCase(tableName) + ".list.html";

		FileManager.replaceTextInFile(componentListPath, "PASCAL_CASE_SPLIT[tableName]",
				FieldNameFormatter.splitCamelCaseToString(tableName));

		FileManager.replaceTextInFile(componentListPath, "CAMEL_CASE[tableName]", TextUtil.capitalizeText(TextUtil.convertToCamelCase(tableName)));

		FileManager.replaceTextInFile(componentListPath, "SNAKE_CASE[tableName]", FieldNameFormatter.toSnakeCase(tableName));

		FileManager.replaceTextInFile(componentListPath, "PASCAL_CASE[tableName]",
				FieldNameFormatter.toPascalCase(tableName));
		FileManager.replaceTextInFile(componentListPath, "[tableHtml]", TemplatePrimeNG.createTable(table) );
		
		/*
		 * COMPONENT FORM 
		 */

		String createComponentPath = componentFolder + "\\form";
		
		FileManager.copyDir(
				PropertiesReading.folder_codegenerator_util + "//hexagonal/FrontEnd/component/form",
				createComponentPath, false);
		
		FileManager.renameMultipleFilesInFolder(createComponentPath, "[tableName]", TextUtil.convertToSnakeCase(tableName));
		
		createComponentPath = componentFolder + "\\form\\" + TextUtil.convertToSnakeCase(tableName) + ".form.ts";
		
		FileManager.replaceTextInFile(createComponentPath, "PASCAL_CASE[tableName]",
				FieldNameFormatter.toPascalCase(tableName));

		FileManager.replaceTextInFile(createComponentPath, "CAMEL_CASE[tableName]", TextUtil.convertToCamelCase(tableName));

		FileManager.replaceTextInFile(createComponentPath, "SNAKE_CASE[tableName]", FieldNameFormatter.toSnakeCase(tableName));
		
		StringBuilder builder = new StringBuilder();
		 for (Column col : table.getColumns()) {
			builder.append("\t\t\t"+col.getName() +": new FormControl(''),\n");
		}
		  
		FileManager.replaceTextInFile(createComponentPath, "[formControls]", builder.toString());
		
		
		createComponentPath = componentFolder + "\\form\\" + TextUtil.convertToSnakeCase(tableName) + ".form.html";

		FileManager.replaceTextInFile(createComponentPath,  "PASCAL_CASE_SPLIT[tableName]",
				FieldNameFormatter.splitCamelCaseToString(tableName));

		FileManager.replaceTextInFile(createComponentPath, "CAMEL_CASE[tableName]", TextUtil.convertToCamelCase(tableName));

		FileManager.replaceTextInFile(createComponentPath, "SNAKE_CASE[tableName]", FieldNameFormatter.toSnakeCase(tableName));
		
		
		FileManager.replaceTextInFile(createComponentPath, "[formHtml]", TemplatePrimeNG.createForm(table) );

		
		
		/*
		 * SCSS
		 */
//		String scssPath = componentFolder + "\\" + TextUtil.convertToSnakeCase(tableName) + ".component.scss";
//
//		FileManager.copyDir(
//				PropertiesReading.folder_codegenerator_util +  (this.architecture.equals("hexagonal") ? "//hexagonal" : "//mvc") + "/FrontEnd/component/[tableName].component.scss",
//				scssPath, false);

		/*
		 * ROUTES
		 */
		String routesPath = componentFolder + "\\" +"routes.ts";
		
		FileManager.copyDir(PropertiesReading.folder_codegenerator_util +  "//hexagonal/FrontEnd/component/routes.ts",
				routesPath, false);

		FileManager.replaceTextInFile(routesPath, "PASCAL_CASE[tableName]",
				FieldNameFormatter.toPascalCase(tableName));

		FileManager.replaceTextInFile(routesPath, "CAMEL_CASE[tableName]", FieldNameFormatter.toCamelCase(tableName));

		FileManager.replaceTextInFile(routesPath, "SNAKE_CASE[tableName]", FieldNameFormatter.toSnakeCase(tableName));

		/*
		 * HTML
		 */
//		String htmlPath = componentFolder + "\\" + TextUtil.convertToSnakeCase(tableName) + ".component.html";
//
//		FileManager.copyDir(
//				PropertiesReading.folder_codegenerator_util +  (this.architecture.equals("hexagonal") ? "//hexagonal" : "//mvc") + "/FrontEnd/component/[tableName].component.html",
//				htmlPath, false);
//		
//		FileManager.replaceTextInFile(htmlPath, "CAMEL_CASE_CAP[tableName]",
//				TextUtil.capitalizeText(TextUtil.convertToCamelCase(tableName)));
//
//		FileManager.replaceTextInFile(htmlPath, "CAMEL_CASE[tableName]", TextUtil.convertToCamelCase(tableName));
//
//		// crear columnas de la tabla
//
//		String columnsTable = "";
//		String fieldRows = "";
//
//		for (Column column : table.getColumns()) {
//			String[] partes = TextUtil.capitalizeText(TextUtil.convertToCamelCase(column.getName())).split("(?=[A-Z])");
//			String columnName = Arrays.stream(partes).collect(Collectors.joining(" "));
//			columnsTable += "\t\t\t\t\t\t\t\t<th>" + columnName + "</th>\n";
//			fieldRows    += "\t\t\t\t\t\t\t\t<td>{{" + TextUtil.convertToCamelCase(tableName) + "."+ TextUtil.convertToCamelCase(column.getName()) + "}}</td>\n";
//		}
//
//		FileManager.replaceTextInFile(htmlPath, "[columnsTable]", columnsTable);
//		FileManager.replaceTextInFile(htmlPath, "[fields_row]", fieldRows);
		return true;
	}

	public Boolean configurar() {

		String option = "";
//
		for (Object[] table : tables) {
			String tableName = (String) table[0];
			option += "\t\t\t\t\t{ label: '"+FieldNameFormatter.splitCamelCaseToString(tableName)
			+"', icon: 'pi pi-fw pi-user', routerLink: ['/"+FieldNameFormatter.toSnakeCase(tableName)+"/list'] },\n";
		}
//		
		String navClass = packagePath + "\\layout\\component\\"+ "app.menu.ts";
		FileManager.replaceTextInFile(navClass, "//ArrayOptions", option);
		
		String routes = "";
		for (Object[] table : tables) {
			String tableName = (String) table[0];
			routes += "\t\t{ path: '"+TextUtil.convertToSnakeCase(tableName)+"', loadChildren: () => import('./app/pages/"+TextUtil.convertToSnakeCase(tableName)+"/"+"routes') },\n";
		}
		
		String routesClass = packagePath.replace("\\app", "") + "\\"+ "app.routes.ts";
		
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


	@Override
	public Boolean generateComponent(String tableName, List<Column> columns) {
		// TODO Auto-generated method stub
		return null;
	}


}