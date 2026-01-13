package com.codegenerator.generator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
	Boolean existProject = false;

	public FrontEndGenerator2(String server, String databaseName, Set<Object[]> tables, JDBCManager jdbcManager,
			String workspace, String projectName, String packageName, boolean addOAuth2, String architecture,
			Boolean existProject) {
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
		this.existProject = existProject;
	}

	@Override
	public Boolean generate() {
		setProcessProgress(0);
		printLog("Generando...");
		printLog("Creando Directorio para frontend..");
		FileManager.createFolder(workspace, projectName);
		setProcessProgress(30);

		if (!existProject) {
			FileManager.copyDir((PropertiesReading.folder_codegenerator_util
					+ (this.architecture.equals("hexagonal") ? "//hexagonal" : "//mvc") + "/FrontEnd/[projectName]"),
					workspace + "\\" + projectName, true);
			FileManager.replaceTextInFilesFolder(workspace + "\\" + projectName, "[projectName]", projectName);
		}

		Set<Object> schemas = tables.stream().filter(arr -> arr.length > 0).map(arr -> arr[0])
				.collect(Collectors.toSet());

		String packageNameComponent = "";
		String packageNameSevice = "";

		for (Object schema : schemas) {
			String schameName = ((String) schema).toLowerCase();
			if (schameName.equals("dbo")) {
				schameName = "";
			}

			packageNameComponent = this.packagePath + "\\pages\\"
					+ TextUtil.convertToSnakeCase((String) (schameName.equals("") ? "" : schameName));

			packageNameSevice = this.packagePath + "\\service\\"
					+ TextUtil.convertToSnakeCase((String) (schameName.equals("") ? "" : schameName));

			FileManager.createPackage(this.packagePath,
					".pages." + TextUtil.convertToSnakeCase((String) (schameName.equals("") ? "" : schameName)));
			FileManager.createPackage(this.packagePath,
					".service." + TextUtil.convertToSnakeCase((String) (schameName.equals("") ? "" : schameName)));

			Set<Object[]> tablesBySchema = tables.stream().filter(arr -> arr.length > 0 && (arr[0]).equals(schema))
//					.map(arr -> arr[1])
					.collect(Collectors.toSet());
			int availableTime = 50;
			int i = 30;
			if (!tablesBySchema.isEmpty()) {
				for (Object[] table : tablesBySchema) {
					i += (availableTime / (tablesBySchema.size()));
					setProcessProgress(i);
					String tableName = (String) table[1];
					printLog("Obteniendo columnas de la tabla " + tableName + "");

					List<Column> columns = jdbcManager.getColumnsByTable(databaseName, tableName);
					Table tbl = new Table();
					tbl.setSchema(schameName);
					tbl.setName(tableName);
					tbl.setColumns(columns);

					if (((Boolean) table[2])) {
						printLog("Generando servicio de la tabla " + tableName + "");
						generateService(packageNameSevice, tableName);
						printLog("Generando componente de la tabla " + tableName + "");
						generateComponent(packageNameComponent, tbl);
					}
				}
			}

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
	public Boolean generateService(String packageNameSevice, String tableName) {
//		FileManager.createFolder(packagePath + "\\service\\", TextUtil.convertToSnakeCase(tableName ));

		String pathService = packageNameSevice + "\\" + TextUtil.convertToSnakeCase(tableName) + ".service.ts";

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
	public Boolean generateComponent(String packageNameComponent, Table table) {

		String tableName = table.getName();
		String componentFolder = packageNameComponent + "\\" + FieldNameFormatter.toSnakeCase(tableName);
		FileManager.createFolder(packageNameComponent, TextUtil.convertToSnakeCase(tableName));

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
		FileManager.replaceTextInFile(componentListPath, "SCHEMA_NAME",
				FieldNameFormatter.toSnakeCase(table.getSchema()));

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
		FileManager.replaceTextInFile(createComponentPath, "SCHEMA_NAME",
				FieldNameFormatter.toSnakeCase(table.getSchema()));

		StringBuilder builder = new StringBuilder();

		List<Column> pkColumnsForm = table.getColumns().stream().filter(user -> user.getIsPrimaryKey())
				.collect(Collectors.toList());
		List<Column> fkColumnsForm = table.getColumns().stream().filter(user -> user.getIsForeigKey())
				.collect(Collectors.toList());

		if (pkColumnsForm.size() > 1) {
			List<Column> columnsAditionalsForm = table.getColumns().stream()
					.filter(user -> !user.getIsPrimaryKey() && !user.getIsForeigKey()).collect(Collectors.toList());

			if (fkColumnsForm.size() == pkColumnsForm.size()) {

				for (Column col : fkColumnsForm) {
					builder.append("\t\t\t\t" + col.getName() + ": " + getFormControl(col) + ",\n");
				}

				for (Column col : columnsAditionalsForm) {
					builder.append("\t\t\t\t" + col.getName() + ": " + getFormControl(col) + ",\n");
				}

			} else {
				if (fkColumnsForm.size() < pkColumnsForm.size()) {
					List<Column> columnsPKNotFKForm = pkColumnsForm.stream().filter(user -> !user.getIsForeigKey())
							.collect(Collectors.toList());
					for (Column col : columnsPKNotFKForm) {
						builder.append("\t\t\t\t" + col.getName() + ": " + getFormControl(col) + ",\n");
					}

					for (Column col : fkColumnsForm) {
						builder.append("\t\t\t\t" + col.getName() + ": " + getFormControl(col) + ",\n");
					}
					for (Column col : columnsAditionalsForm) {
						builder.append("\t\t\t\t" + col.getName() + ": " + getFormControl(col) + ",\n");
					}
				}
			}

		} else {
			for (Column col : table.getColumns()) {
				builder.append("\t\t\t\t" + col.getName() + ": " + getFormControl(col) + ",\n");
			}
		}

		FileManager.replaceTextInFile(createComponentPath, "[formControls]", builder.toString());

		StringBuilder importsService = new StringBuilder("");
		StringBuilder declarationsOption = new StringBuilder("");
		StringBuilder declarationsService = new StringBuilder("");
		StringBuilder executionsService = new StringBuilder("");

		int i = 0;
		List<Column> fkColumns = table.getColumns().stream().filter(user -> user.getIsForeigKey())
				.collect(Collectors.toList());

		if (tableName.equals("Issuer"))
			System.out.println();

		if (!fkColumns.isEmpty()) {
			String foreignKeyColumn = "";
			String foreignKeyColumnOld = "";
			for (Column col : fkColumns) {
				String fkName = "";
				if (col.getName().startsWith("id"))
					fkName = col.getName().substring(2);
				else if (col.getName().endsWith("_id") || col.getName().endsWith("Id"))
					fkName = col.getName().replace("_id", "").replace("Id", "");

				Optional<Object[]> fk = tables.stream().filter(tbl -> tbl[1].equals(col.getTableReference()))
						.findFirst();
				String schemaFK = fk.get()[0].toString().toLowerCase();

				foreignKeyColumn = FieldNameFormatter.formatText(col.getTableReference(), false);

				if (!foreignKeyColumnOld.equals(foreignKeyColumn) && !FieldNameFormatter.formatText(tableName, false).equals(foreignKeyColumn) ) {
					importsService.append("import { " + FieldNameFormatter.toPascalCase(col.getTableReference()) + "Service }" 
							+ " from '../../../../service/"
							+ TextUtil.convertToSnakeCase((String) (schemaFK.equals("") ? "" : schemaFK)) + "/"
							+ FieldNameFormatter.toSnakeCase(col.getTableReference()) + ".service';\n");

					foreignKeyColumnOld = foreignKeyColumn;
				}

				declarationsOption
						.append("\topts" + FieldNameFormatter.toPascalCase(fkName) + " : any[] | undefined; \n");
				declarationsService.append("private " + FieldNameFormatter.toCamelCase(fkName) + "Service: "
						+ FieldNameFormatter.toPascalCase(col.getTableReference()) + "Service,\n");
				executionsService.append("	this." + FieldNameFormatter.toCamelCase(fkName)
						+ "Service.findAll().subscribe((data: any) => {\r\n" + "			this.opts"
						+ FieldNameFormatter.toPascalCase(fkName) + " = data\r\n" + "	},\r\n"
						+ "		(error: any) => {\r\n" + "		});\n");
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

		FileManager.replaceTextInFile(mainComponentPath, "KEBAB_CASE[tableName]",
				FieldNameFormatter.toKebabCase(tableName));

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
			return "new FormControl(undefined" + (!column.getIsNullable() ? ", Validators.required" : "") + ")";

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

		Set<Object> schemas = tables.stream().filter(arr -> arr.length > 0).map(arr -> arr[0])
				.collect(Collectors.toSet());

		StringBuilder query = new StringBuilder();
		String option = "";
		String routes = "";
		int id = 1;
		int i = 1;
		for (Object schema : schemas) {
			String schameName = ((String) schema).toLowerCase();
			if (schameName.equals("dbo")) {
				schameName = "";
				continue;
			}

			option += "\t\t\t\t\t{\n " + " label: '" + FieldNameFormatter.splitCamelCaseToString(schameName) + "',\n"
					+ " icon: 'pi pi-fw pi-user',\n" + " items: [\n";
			List<Object> tablesBySchema = tables.stream().filter(arr -> arr.length > 0 && (arr[0]).equals(schema))
					.map(arr -> arr[1]).collect(Collectors.toList());

			query.append(
					"INSERT INTO [Security].[Access]([id],[name] ,[description] ,[icon] ,[url] ,[parentAccessId] ,[menuOrder] ,[nameEN] ,[nameFR] ,[enabled] ,[createdBy] ,[createdDate])");
			query.append("VALUES ");
			query.append(" ( " + id + "");
			query.append(" , '" + schameName + "' ");
			query.append(", '' ");
			query.append(", 'pi pi-fw pi-user' ");
			query.append(", null ");
			query.append(", null ");
			query.append(", " + i + "");
			query.append(", null  ");
			query.append(", null ");
			query.append(", 1 ");
			query.append(", 1 ");
			query.append(", GETDATE()");
			query.append(" ) \n");
			query.append("GO\n");

			if (!tablesBySchema.isEmpty()) {

				int j = 1;
				for (Iterator iterator = tablesBySchema.iterator(); iterator.hasNext();) {
					Object object = (Object) iterator.next();
					id++;
					option += "\t\t\t\t\t{ label: '" + FieldNameFormatter.splitCamelCaseToString(object.toString())
							+ "', icon: 'pi pi-fw pi-user', routerLink: ['/"
							+ FieldNameFormatter.toSnakeCase(object.toString()) + "'] },\n";

					routes += "\t\t\t{ path: '" + TextUtil.convertToSnakeCase(object.toString())
							+ "', loadChildren: () => import('./app/pages/" + schameName + "/"
							+ TextUtil.convertToSnakeCase(object.toString()) + "/" + "routes') },\n";

					query.append(
							"INSERT INTO [Security].[Access]([id],[name] ,[description] ,[icon] ,[url] ,[parentAccessId] ,[menuOrder] ,[nameEN] ,[nameFR] ,[enabled] ,[createdBy] ,[createdDate])");
					query.append("VALUES ");
					query.append(" ( " + id + "");
					query.append(" , '" + FieldNameFormatter.splitCamelCaseToString(object.toString()) + "' ");
					query.append(", '' ");
					query.append(", 'pi pi-fw pi-user' ");
					query.append(", '/" + FieldNameFormatter.toSnakeCase(object.toString()) + "' ");
					query.append(", " + i + "");
					query.append(", " + j + "");
					query.append(", null  ");
					query.append(", null ");
					query.append(", 1 ");
					query.append(", 1 ");
					query.append(", GETDATE()  ");
					query.append(" ) \n");
					query.append("GO\n");

					j++;

				}
			}

			i++;
			id++;
			option += " ]" + "\t\t\t\t\t },\n";
		}

		System.out.println(query.toString());
//		String option = "";
//		for (Object[] table : tables) {
//			String tableName = (String) table[1];
//			option += "\t\t\t\t\t{ label: '" + FieldNameFormatter.splitCamelCaseToString(tableName)
//					+ "', icon: 'pi pi-fw pi-user', routerLink: ['/" + FieldNameFormatter.toSnakeCase(tableName)
//					+ "'] },\n";
//		}

		String navClass = packagePath + "\\layout\\component\\" + "app.menu.ts";
		FileManager.replaceTextInFile(navClass, "//ArrayOptions", option);

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

	@Override
	public Boolean generateService(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean generateComponent(Table table) {
		// TODO Auto-generated method stub
		return null;
	}

}