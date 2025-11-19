package com.codegenerator.generator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.codegenerator.connection.JDBCManager;
import com.codegenerator.generator.template.TemplatePrimeNG;
import com.codegenerator.util.Column;
import com.codegenerator.util.FieldNameFormatter;
import com.codegenerator.util.FileManager;
import com.codegenerator.util.PropertiesReading;
import com.codegenerator.util.Table;
import com.codegenerator.util.TextUtil;

public class FrontEndGenerator2 implements IFrontEndGenerator {

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
		FileManager.copyDir(
				PropertiesReading.folder_codegenerator_util
						+ (this.architecture.equals("hexagonal") ? "//hexagonal" : "//mvc") + "/FrontEnd/[projectName]",
				workspace + "\\" + projectName, true);
		FileManager.replaceTextInFilesFolder(workspace + "\\" + projectName, "[projectName]", projectName);

		int availableTime = 50;
		int i = 30;
		for (Object[] table : tables) {
			i += (availableTime / (tables.size()));
			setProcessProgress(i);
			String tableName = (String) table[0];
			printLog("Obteniendo columnas de la tabla " + tableName + "");

			List<Column> columns = jdbcManager.getColumnsByTable(databaseName, tableName);
			Table tbl = new Table();
			tbl.setName(tableName);
			tbl.setColumns(columns);
			printLog("Generando servicio de la tabla " + tableName + "");
			generateService(tableName);
			printLog("Generando componente de la tabla " + tableName + "");
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

		String pathService = packagePath + "\\pages\\service\\" + TextUtil.convertToSnakeCase(tableName)
				+ ".service.ts";

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
		FileManager.createFolder(packagePath + "\\pages\\", TextUtil.convertToSnakeCase(tableName));

		String componentFolder = packagePath + "\\pages\\" + FieldNameFormatter.toSnakeCase(tableName);

		/**
		 * COMPONENT_LIST
		 */
		String componentListPath = componentFolder + "\\list";

		FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/hexagonal/FrontEnd/component/list",
				componentListPath, false);

		FileManager.renameMultipleFilesInFolder(componentListPath, "[tableName]",
				TextUtil.convertToSnakeCase(tableName));

		componentListPath = componentFolder + "\\list\\" + TextUtil.convertToSnakeCase(tableName) + ".list.ts";

		FileManager.replaceTextInFile(componentListPath, "PASCAL_CASE[tableName]",
				FieldNameFormatter.toPascalCase(tableName));
		FileManager.replaceTextInFile(componentListPath, "KEBAB_CASE[tableName]",
				FieldNameFormatter.toKebabCase(tableName));
		FileManager.replaceTextInFile(componentListPath, "CAMEL_CASE[tableName]",
				FieldNameFormatter.toCamelCase(tableName));
		FileManager.replaceTextInFile(componentListPath, "SNAKE_CASE[tableName]",
				FieldNameFormatter.toSnakeCase(tableName));

		componentListPath = componentFolder + "\\list\\" + TextUtil.convertToSnakeCase(tableName) + ".list.html";

		FileManager.replaceTextInFile(componentListPath, "PASCAL_CASE_SPLIT[tableName]",
				FieldNameFormatter.splitCamelCaseToString(tableName));
		FileManager.replaceTextInFile(componentListPath, "CAMEL_CASE[tableName]",
				TextUtil.capitalizeText(TextUtil.convertToCamelCase(tableName)));
		FileManager.replaceTextInFile(componentListPath, "SNAKE_CASE[tableName]",
				FieldNameFormatter.toSnakeCase(tableName));
		FileManager.replaceTextInFile(componentListPath, "PASCAL_CASE[tableName]",
				FieldNameFormatter.toPascalCase(tableName));

		FileManager.replaceTextInFile(componentListPath, "[tableHtml]", TemplatePrimeNG.createTable(table));

		/*
		 * COMPONENT FORM
		 */

		String createComponentPath = componentFolder + "\\form";
		FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "//hexagonal/FrontEnd/component/form",
				createComponentPath, false);
		FileManager.renameMultipleFilesInFolder(createComponentPath, "[tableName]",
				TextUtil.convertToSnakeCase(tableName));

		createComponentPath = componentFolder + "\\form\\" + TextUtil.convertToSnakeCase(tableName) + ".form.ts";
		FileManager.replaceTextInFile(createComponentPath, "PASCAL_CASE[tableName]",
				FieldNameFormatter.toPascalCase(tableName));
		FileManager.replaceTextInFile(createComponentPath, "KEBAB_CASE[tableName]",
				FieldNameFormatter.toKebabCase(tableName));
		FileManager.replaceTextInFile(createComponentPath, "CAMEL_CASE[tableName]",
				FieldNameFormatter.toCamelCase(tableName));
		FileManager.replaceTextInFile(createComponentPath, "SNAKE_CASE[tableName]",
				FieldNameFormatter.toSnakeCase(tableName));

		StringBuilder builder = new StringBuilder();
		for (Column col : table.getColumns()) {

			
			builder.append("\t\t\t\t" + col.getName() + ": " + getFormControl(col) + ",\n");
		}

		FileManager.replaceTextInFile(createComponentPath, "[formControls]", builder.toString());

		StringBuilder importsService = new StringBuilder("");
		StringBuilder declarationsOption = new StringBuilder("");
		StringBuilder declarationsService = new StringBuilder("");
		StringBuilder executionsService = new StringBuilder("");

		int i = 0;
		List<Column> x = table.getColumns().stream().filter(user -> user.getIsForeigKey()).collect(Collectors.toList());

		if (tableName.equals("Usuario"))
			System.out.println();

		if (!x.isEmpty()) {
			for (Column col : x) {
				String fkName = "";
				if (col.getName().startsWith("id"))
					fkName = col.getName().substring(2);
				else if (col.getName().endsWith("_id") || col.getName().endsWith("Id"))
					fkName = col.getName().replace("_id", "").replace("Id", "");

				String foreignKeyColumn = FieldNameFormatter.formatText(fkName, false);
				importsService.append(
						"import { " + FieldNameFormatter.toPascalCase(foreignKeyColumn)
						+ "Service } from '../../service/" + FieldNameFormatter.toSnakeCase(foreignKeyColumn)
						+ ".service';\n");
				declarationsOption.append(
						"\topts" + FieldNameFormatter.toPascalCase(fkName) + ": any[] | undefined; \n");
				declarationsService.append("private " + foreignKeyColumn + "Service: "
						+ FieldNameFormatter.toPascalCase(foreignKeyColumn) + "Service,\n");
				executionsService
						.append("	this." + foreignKeyColumn + "Service.findAll().subscribe((data: any) => {\r\n"
								+ "			this.opts" + FieldNameFormatter.toPascalCase(foreignKeyColumn)
								+ " = data\r\n" + "	},\r\n" + "		(error: any) => {\r\n" + "		});\n");
				i++;
			}
		}

		FileManager.replaceTextInFile(createComponentPath, "//importService", importsService.toString());
		FileManager.replaceTextInFile(createComponentPath, "//declarationsOption", declarationsOption.toString());
		FileManager.replaceTextInFile(createComponentPath, "//declarationsService", declarationsService.toString());
		FileManager.replaceTextInFile(createComponentPath, "//executionsService", executionsService.toString());

		createComponentPath = componentFolder + "\\form\\" + TextUtil.convertToSnakeCase(tableName) + ".form.html";

		FileManager.replaceTextInFile(createComponentPath, "PASCAL_CASE_SPLIT[tableName]",
				FieldNameFormatter.splitCamelCaseToString(tableName));
		FileManager.replaceTextInFile(createComponentPath, "CAMEL_CASE[tableName]",
				FieldNameFormatter.toCamelCase(tableName));
		FileManager.replaceTextInFile(createComponentPath, "SNAKE_CASE[tableName]",
				FieldNameFormatter.toSnakeCase(tableName));
		FileManager.replaceTextInFile(createComponentPath, "[formHtml]", TemplatePrimeNG.createForm(table));

		/**
		 * COMPONENT MAIN
		 */
		String mainComponentPath = componentFolder + "\\" + TextUtil.convertToSnakeCase(tableName) + ".ts";

		FileManager.copyDir(
				PropertiesReading.folder_codegenerator_util + "//hexagonal/FrontEnd/component/[tableName].ts",
				mainComponentPath, false);

		FileManager.replaceTextInFile(mainComponentPath, "PASCAL_CASE[tableName]",
				FieldNameFormatter.toPascalCase(tableName));
		FileManager.replaceTextInFile(mainComponentPath, "KEBAB_CASE[tableName]",
				FieldNameFormatter.toKebabCase(tableName));
		FileManager.replaceTextInFile(mainComponentPath, "CAMEL_CASE[tableName]",
				FieldNameFormatter.toCamelCase(tableName));
		FileManager.replaceTextInFile(mainComponentPath, "SNAKE_CASE[tableName]",
				FieldNameFormatter.toSnakeCase(tableName));

		mainComponentPath = componentFolder + "\\" + TextUtil.convertToSnakeCase(tableName) + ".html";

		FileManager.copyDir(
				PropertiesReading.folder_codegenerator_util + "//hexagonal/FrontEnd/component/[tableName].html",
				mainComponentPath, false);

		FileManager.replaceTextInFile(mainComponentPath, "KEBAB_CASE[tableName]", FieldNameFormatter.toKebabCase(tableName));

		/*
		 * ROUTES
		 */
		String routesPath = componentFolder + "\\" + "routes.ts";

		FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "//hexagonal/FrontEnd/component/routes.ts",
				routesPath, false);

		FileManager.replaceTextInFile(routesPath, "PASCAL_CASE[tableName]", FieldNameFormatter.toPascalCase(tableName));

		FileManager.replaceTextInFile(routesPath, "CAMEL_CASE[tableName]", FieldNameFormatter.toCamelCase(tableName));

		FileManager.replaceTextInFile(routesPath, "SNAKE_CASE[tableName]", FieldNameFormatter.toSnakeCase(tableName));

		return true;
	}

	public String getFormControl(Column column) {

		if (column.getIsPrimaryKey())
			return "new FormControl(undefined)";
		
		if (column.getIsForeigKey())
			return "new FormControl(undefined"+ (!column.getIsNullable() ? ", Validators.required" : "") + ")";

		switch (column.getDataType()) {
		case "int":
		case "bigint":
		case "smallint":
		case "tinyint":
		case "decimal":
		case "numeric":
		case "float":
		case "real":
			return "new FormControl(undefined" + (!column.getIsNullable() ? ", Validators.required" : "") + ")";
		case "bit":
			return "new FormControl(false" + (!column.getIsNullable() ? ", Validators.required" : "") + ")";
		case "char":
		case "nchar":
		case "varchar":
		case "nvarchar":
		case "text":
		case "ntext":
			return "new FormControl(''" + (!column.getIsNullable() ? ", Validators.required" : "") + ")";
		case "date":
		case "datetime":
		case "datetime2":
		case "smalldatetime":
		case "time":
		case "timestamp":
			return "new FormControl(new Date()" + (!column.getIsNullable() ? ", Validators.required" : "") + ")";

		case "binary":
		case "varbinary":
		case "image":
			return "new FormControl()";
		default:
			return "<!-- Tipo no reconocido: " + column.getDataType() + " -->";
		}

	}

	public Boolean configurar() {

		String option = "";
		for (Object[] table : tables) {
			String tableName = (String) table[0];
			option += "\t\t\t\t\t{ label: '" + FieldNameFormatter.splitCamelCaseToString(tableName)
					+ "', icon: 'pi pi-fw pi-user', routerLink: ['/" + FieldNameFormatter.toSnakeCase(tableName)
					+ "'] },\n";
		}

		String navClass = packagePath + "\\layout\\component\\" + "app.menu.ts";
		FileManager.replaceTextInFile(navClass, "//ArrayOptions", option);

		String routes = "";
		for (Object[] table : tables) {
			String tableName = (String) table[0];
			routes += "\t\t\t{ path: '" + TextUtil.convertToSnakeCase(tableName)
					+ "', loadChildren: () => import('./app/pages/" + TextUtil.convertToSnakeCase(tableName) + "/"
					+ "routes') },\n";
		}

		String routesClass = packagePath.replace("\\app", "") + "\\" + "app.routes.ts";

		FileManager.replaceTextInFile(routesClass, "//ArrayRoutes", routes);

		return true;
	}

	public int getProcessProgress() {
		return processProgress;
	}

	public void setProcessProgress(int processProgress) {
		this.processProgress = processProgress;
	}

	private String log = "";

	public String printLog(String text) {
		this.log = getDateTime() + " - " + text + "\n";
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