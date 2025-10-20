package com.codegenerator.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import com.codegenerator.connection.JDBCManager;
import com.codegenerator.util.Column;
import com.codegenerator.util.DataTypeConverter;
import com.codegenerator.util.FieldNameFormatter;
import com.codegenerator.util.FileManager;
import com.codegenerator.util.PropertiesReading;
import com.codegenerator.util.ScriptRunner;
import com.codegenerator.util.Table;


public class BackEndGenerator {

	Set<Object[]> tables;
	JDBCManager jdbcManager;
	String packageName;
	String workspace;
	private String projectName;
	String packagePath = "";
	String resourcesPath = "";
	String databaseName;
	String server = "";
	String architecture = "mvc";

	private int processProgress = 0;

	boolean addOAuth2;

	public BackEndGenerator(String server, String databaseName, Set<Object[]> tables, JDBCManager jdbcManager,
			String workspace, String projectName, String packageName, String architecture) {
		this.databaseName = databaseName;
		this.tables = tables;
		this.jdbcManager = jdbcManager;
		this.packageName = packageName;
		this.workspace = workspace;
		this.projectName = projectName;
		this.packagePath = workspace + "\\" + projectName + "\\src\\main\\java";
		this.resourcesPath = workspace + "\\" + projectName + "\\src\\main\\resources";
		this.server = server;
		this.architecture = architecture;
	}

	public BackEndGenerator(String server, String databaseName, Set<Object[]> tables, JDBCManager jdbcManager,
			String workspace, String projectName, String packageName, boolean addOAuth2) {
		this.databaseName = databaseName;
		this.tables = tables;
		this.jdbcManager = jdbcManager;
		this.packageName = packageName;
		this.workspace = workspace;
		this.projectName = projectName;
		this.packagePath = workspace + "\\" + projectName + "\\src\\main\\java";
		this.resourcesPath = workspace + "\\" + projectName + "\\src\\main\\resources";
		this.server = server;
		this.addOAuth2 = addOAuth2;
	}

	public boolean generar() {
		setProcessProgress(0);
		printLog("Generando...");
		try {

			String packageNameEntity = "";
			String packageNameModel = "";
			String packageNameDTO = "";
			String packageNameRepository = "";
			String packageNameRepositoryImpl = "";
			String packageNameSevice = "";
			String packageNameServiceImpl = "";
			String packageNameController = "";

			printLog("Creando Directorio para backend..");

			FileManager.createRootDirectory(workspace, projectName);
			FileManager.createPackage(this.packagePath, this.packageName);

			if (this.architecture.equals("mvc")) {

				packageNameEntity = this.packageName + ".model";
				packageNameRepository = this.packageName + ".repository";
				packageNameRepositoryImpl = this.packageName + ".repository.impl";
				packageNameSevice = this.packageName + ".service";
				packageNameServiceImpl = this.packageName + ".service.impl";
				packageNameController = this.packageName + ".controller";

				FileManager.createPackage(this.packagePath, this.packageName + ".configuration");
				FileManager.createPackage(this.packagePath, this.packageName + ".model");
				FileManager.createPackage(this.packagePath, this.packageName + ".repository.impl");
				FileManager.createPackage(this.packagePath, this.packageName + ".service.impl");
				FileManager.createPackage(this.packagePath, this.packageName + ".controller");
			} else if (this.architecture.equals("hexagonal")) {

				packageNameEntity = this.packageName + ".infrastructure.adapters.output.persistence.entity";
				packageNameRepository = this.packageName + ".application.ports.output";
				packageNameRepositoryImpl = this.packageName + ".infrastructure.adapters.output.persistence";
				packageNameSevice = this.packageName + ".application.ports.input";
				packageNameServiceImpl = this.packageName + ".application.usecases";
				packageNameController = this.packageName + ".infrastructure.adapters.input.rest";
				packageNameModel = this.packageName + ".domain.model";
				packageNameDTO = this.packageName + ".infrastructure.adapters.input.rest.dto";

				FileManager.createPackage(this.packagePath, this.packageName + ".configuration");
				FileManager.createPackage(this.packagePath, packageNameEntity);
				FileManager.createPackage(this.packagePath, packageNameRepositoryImpl);
				FileManager.createPackage(this.packagePath, packageNameSevice);
				FileManager.createPackage(this.packagePath, packageNameRepository);
				FileManager.createPackage(this.packagePath, packageNameServiceImpl);
				FileManager.createPackage(this.packagePath, packageNameModel);
				FileManager.createPackage(this.packagePath, packageNameController);
				FileManager.createPackage(this.packagePath, packageNameDTO);
			}

			setProcessProgress(30);
			for (Object[] table : tables) {
				String tableName = (String) table[0];
				printLog("Obteniendo columnas de la tabla " + tableName + "");
				List<Column> columns = jdbcManager.getColumnsByTable(databaseName, tableName);

				List<Column> col = columns.stream().filter(x -> x.getIsPrimaryKey()).toList();

				if (col.size() == 1) {
					Table tbl = new Table();
					tbl.setName(tableName);
					tbl.setColumns(columns);
					printLog("Generando modelo de la tabla " + tableName + "");

					generateEntity(packageNameEntity, tableName, columns);
					generateModel(packageNameModel, tableName, columns);
					generateDTO(packageNameDTO, tableName, columns);
					printLog("Generando repositorio de la tabla " + tableName + "");
					generateRepository(packageNameEntity, packageNameRepository, tableName);
					generateRepositoryImpl(packageNameEntity, packageNameRepository, packageNameRepositoryImpl,
							tableName);
					printLog("Generando servicio de la tabla " + tableName + "");
					generateService(packageNameEntity, packageNameSevice, tableName);
					generateServiceImpl(packageNameEntity, packageNameSevice, packageNameServiceImpl, tableName);

					printLog("Generando controlador de la tabla " + tableName + "");
					generateController(packageNameDTO, packageNameController, tableName);
				}
			}

			setProcessProgress(50);
			printLog("Preparando carpeta util...");
			// Preparar carpteta util
			String folderSrcUtil = PropertiesReading.folder_codegenerator_util
					+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "//mvc") + "//util";
			System.out.println(folderSrcUtil);
			String folderTrgUtil = packagePath + "\\" + packageName.replace(".", "\\") + "\\util";
			FileManager.copyDir(folderSrcUtil, folderTrgUtil, false);

			FileManager.replaceTextInFilesFolder(packagePath + "\\" + packageName.replace(".", "\\") + "\\util",
					"[packageName]", packageName);

			printLog("Preparando clase principal de spring Boot Application.java...");

			// Preparar clase principal de spring Boot Application.java
			FileManager.copyDir(PropertiesReading.folder_codegenerator_util
					+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "//mvc") + "/Application.java",
					packagePath + "\\" + packageName.replace(".", "\\") + "\\Application.java", false);

			FileManager.replaceTextInFile(packagePath + "\\" + packageName.replace(".", "\\") + "\\Application.java",
					"[packageName]", packageName);
			setProcessProgress(60);
			printLog("Preparando configuración hibernate...");

			// preparar configuracion Hibernate
			FileManager.copyDir(PropertiesReading.folder_codegenerator_util
					+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "//mvc") + "/configuration",
					packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration", false);

			StringBuilder builder = new StringBuilder();
			
			for (Object[] table : tables) {
				String tableName = (String) table[0];
				builder.append("auth.requestMatchers(\"/"+ FieldNameFormatter.toPascalCase(tableName)+"/**\").permitAll();");
			}
			
			FileManager.replaceTextInFilesFolder(
					packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration\\SecurityConfig.java",
					"//requestMatchers",
					builder.toString());

			FileManager.replaceTextInFilesFolder(
					packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration", "[packageName]",
					packageName);

			setProcessProgress(70);
			if (this.addOAuth2) {

				printLog("Agregando Spring Security Oauth2...");
				FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/mvc/security/pom.xml",
						workspace + "\\" + projectName + "\\pom.xml", false);

				FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/mvc/security/configuration",
						packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration", false);

				FileManager.replaceTextInFilesFolder(
						packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration", "[packageName]",
						packageName);

				printLog("\tCreando tablas requeridas por Spring Security Oauth2...");
				addTablesSpringSecurity(databaseName);

				printLog("\tCreando modelo user para Spring Security Oauth2...");
				FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/mvc/security/model",
						packagePath + "\\" + packageName.replace(".", "\\") + "\\model", false);

				FileManager.replaceTextInFilesFolder(packagePath + "\\" + packageName.replace(".", "\\") + "\\model",
						"[packageName]", packageName);

				setProcessProgress(75);
				printLog("\tCreando repository user para Spring Security Oauth2...");
				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util + "/mvc/security/repository/UserRepository.java",
						packagePath + "\\" + packageName.replace(".", "\\") + "\\repository\\UserRepository.java",
						false);

				FileManager.replaceTextInFilesFolder(
						packagePath + "\\" + packageName.replace(".", "\\") + "\\repository\\UserRepository.java",
						"[packageName]", packageName);

				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util
								+ "/mvc/security/repository/impl/UserRepositoryImpl.java",

						packagePath + "\\" + packageName.replace(".", "\\")
								+ "\\repository\\impl\\UserRepositoryImpl.java",

						false);

				FileManager.replaceTextInFilesFolder(packagePath + "\\" + packageName.replace(".", "\\")
						+ "\\repository\\impl\\UserRepositoryImpl.java", "[packageName]", packageName);
				printLog("\tCreando service user para Spring Security Oauth2...");
//				generateService("User");
				printLog("\tCreando repository authority para Spring Security Oauth2...");
//				generateRepository("Authority");
//				generateService("Authority");

				printLog("\tCreando UserDetailsService para Spring Security Oauth2...");
				FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/mvc/UserDetailsServiceImpl.java",
						packagePath + "\\" + packageName.replace(".", "\\")
								+ "\\service\\impl\\UserDetailsServiceImpl.java",
						false);

				FileManager.replaceTextInFile(packagePath + "\\" + packageName.replace(".", "\\")
						+ "\\service\\impl\\UserDetailsServiceImpl.java", "[packageName]", packageName);
			} else {
				FileManager.copyDir(PropertiesReading.folder_codegenerator_util
						+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "//mvc") + "/pom.xml",
						workspace + "\\" + projectName + "\\pom.xml", false);
			}

			setProcessProgress(80);
			printLog("Preparando archivo pom.xml...");

			// preparar archivo pom.xml
			FileManager.replaceTextInFile(workspace + "\\" + projectName + "\\pom.xml", "[packageName]", packageName);
			FileManager.replaceTextInFile(workspace + "\\" + projectName + "\\pom.xml", "[projectName]", projectName);

			FileManager.replaceTextInFile(workspace + "\\" + projectName + "\\pom.xml", "[DBgroupId]",
					PropertiesReading.getProperty(jdbcManager.getServer() + ".groupId"));
			FileManager.replaceTextInFile(workspace + "\\" + projectName + "\\pom.xml", "[DBartifactId]",
					PropertiesReading.getProperty(jdbcManager.getServer() + ".artifactId"));
			FileManager.replaceTextInFile(workspace + "\\" + projectName + "\\pom.xml", "[DBversion]",
					PropertiesReading.getProperty(jdbcManager.getServer() + ".version"));

			setProcessProgress(90);
			printLog("Preparando archivo application.properties...");
			// preparar archivo application.properties
			FileManager.copyDir(
					PropertiesReading.folder_codegenerator_util
							+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "//mvc") + "/resources",
					resourcesPath, false);

			String url = "jdbc:";
			String prop = jdbcManager.getServer().trim() + ".datasource.driver-class-name";
			String driver = PropertiesReading.getProperty(prop);
			StringBuilder urlDB = new StringBuilder(
					PropertiesReading.getProperty(jdbcManager.getServer() + ".datasource.url.databasename"));
			url = urlDB.toString().replace("?1", jdbcManager.getHost()).replace("?2", jdbcManager.getPort())
					.replace("?3", databaseName);

			FileManager.replaceTextInFile(resourcesPath + "\\application.properties", "[driver]", driver);

			FileManager.replaceTextInFile(resourcesPath + "\\application.properties", "[url]", url);
			FileManager.replaceTextInFile(resourcesPath + "\\application.properties", "[username]",
					jdbcManager.getUsername());

			FileManager.replaceTextInFile(resourcesPath + "\\application.properties", "[password]",
					jdbcManager.getPassword());

			FileManager.replaceTextInFile(resourcesPath + "\\application.properties", "[dialect]",
					PropertiesReading.getProperty(jdbcManager.getServer() + ".dialect"));

			FileManager.replaceTextInFile(resourcesPath + "\\application.properties", "[packageName]", packageName);

			if (this.architecture.equals("hexagonal")) {
				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util
								+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "")
								+ "/auth/AuthController.java",
						packagePath + "\\" + packageNameController.replace(".", "\\") + "\\AuthController.java", false);

				FileManager.replaceTextInFile(
						packagePath + "\\" + packageNameController.replace(".", "\\") + "\\AuthController.java",
						"[packageName]", packageName);
				
				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util
								+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "")
								+ "/auth/AuthService.java",
						packagePath + "\\" + packageNameSevice.replace(".", "\\") + "\\AuthService.java", false);
				
				FileManager.replaceTextInFile(
						packagePath + "\\" + packageNameSevice.replace(".", "\\") + "\\AuthService.java",
						"[packageName]", packageName);

				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util
								+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "")
								+ "/auth/AuthServiceImpl.java",
						packagePath + "\\" + packageNameServiceImpl.replace(".", "\\") + "\\AuthServiceImpl.java",
						false);
				
				FileManager.replaceTextInFile(
						packagePath + "\\" + packageNameServiceImpl.replace(".", "\\") + "\\AuthServiceImpl.java",
						"[packageName]", packageName);

				FileManager.copyDir(PropertiesReading.folder_codegenerator_util
						+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "") + "/auth/AuthDTO.java",
						packagePath + "\\" + packageNameDTO.replace(".", "\\") + "\\AuthDTO.java", false);
				FileManager.replaceTextInFile(
						packagePath + "\\" + packageNameServiceImpl.replace(".", "\\") + "\\AuthDTO.java",
						"[packageName]", packageName);

				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util
								+ (this.architecture.equals("hexagonal") ? "//hexagonal//backend" : "")
								+ "/auth/UserDetailsServiceImpl.java",
						packagePath + "\\" + packageNameServiceImpl.replace(".", "\\")
								+ "\\UserDetailsServiceImpl.java",
						false);
				
				FileManager.replaceTextInFile(
						packagePath + "\\" + packageNameServiceImpl.replace(".", "\\") + "\\UserDetailsServiceImpl.java",
						"[packageName]", packageName);
				
			}

		} catch (Exception e) {
			setProcessProgress(100);
			e.printStackTrace();
			return false;
		}

		setProcessProgress(100);

		return true;
	}

	private String getDataTypeJava(String server, String dataType) {
		String dataTypeJava = "";
		if (server.equals("mysql")) {
			dataTypeJava = DataTypeConverter.mysqlToJava.get(dataType);
		} else {
			dataTypeJava = DataTypeConverter.sqlserverToJava.get(dataType);
		}
		return dataTypeJava;
	}

	private String capitalizeText(String text) {
		text = text.toLowerCase().substring(0, 1).toUpperCase() + text.toLowerCase().substring(1, text.length());
		return text;
	}

	private String formatText(String text, boolean capitalize) {
		String[] data = text.split("_");
		if (data.length > 1) {
			String aux = (capitalize ? data[0].substring(0, 1).toUpperCase() : data[0].substring(0, 1).toLowerCase())
					+ data[0].substring(1, data[0].length());
			for (int i = 1; i < data.length; i++) {
				aux += data[i].substring(0, 1).toUpperCase() + data[i].substring(1, data[i].length());
			}

			text = aux;
		} else {

			text = (capitalize ? text.substring(0, 1).toUpperCase() : text.substring(0, 1).toLowerCase())
					+ text.substring(1, text.length());
		}

		return text;
	}

	private boolean generateEntity(String packageNameEntity, String tableName, List<Column> columns) {
		try {
			List<Column> col = columns.stream().filter(x -> x.getIsPrimaryKey()).toList();

			if (col.size() == 1) {

				String pathModel = packagePath + "\\" + packageNameEntity.replace(".", "\\") + "\\"
						+ formatText(tableName, true) // capitalizeText(tableName)
						+ "Entity.java";
				File f = new File(pathModel);
				if (f.exists()) {
					printLog("");
					return true;
				} else if (columns == null)
					columns = jdbcManager.getColumnsByTable(databaseName, tableName);

				f.createNewFile();
				Writer w = new OutputStreamWriter(new FileOutputStream(f));

				w.append("package " + packageNameEntity + ";\n\n");

				w.append("import jakarta.persistence.*;\n");

				w.append("@Entity\n");
				w.append("@Table(name = \"" + tableName + "\")\n");
				w.append("public class " + formatText(tableName, true) + "Entity { \n\n");

				// Add properties
				for (Column column : columns) {
					if (!column.getIsForeigKey()) {
						if (column.getIsPrimaryKey()) {
							w.append("\t@Id\n");
							w.append("\t@GeneratedValue(strategy= GenerationType.IDENTITY)\n");
						}

						if (column.getDataType().equals("varbinary"))
							w.append("\t@Lob\n");

						String len = "";
						if ((column.getDataType().equals("varchar") || column.getDataType().equals("nvarchar"))
								&& column.getLength() != -1)
							len = ", length = " + column.getLength() + "";

						w.append("\t@Column(name = \"" + column.getName() + "\"" + len + ")\n");
						w.append("\tprivate " + getDataTypeJava(this.server, column.getDataType()) + " "
								+ formatText(column.getName(), false) + ";\n\n");
					} else {
						String fkName = "";
						if (column.getName().startsWith("id"))
							fkName = column.getName().substring(2);
						else if (column.getName().endsWith("_id") || column.getName().endsWith("Id"))
							fkName = column.getName().replace("_id", "").replace("Id", "");
						String foreignKeyColumn = formatText(fkName, true);
						w.append("\t@ManyToOne\n");
						w.append("\t@JoinColumn(name = \"" + column.getName() + "\")\n");
						w.append(
								"\tprivate " + foreignKeyColumn + "Entity" + " " + formatText(fkName, false) + ";\n\n");
					}
				}

				// Add constructor

				w.append("\tpublic " + formatText(tableName, true) + "Entity(){\n");
				w.append("\t}\n\n");

				// Add method setters and getters

				for (Column column : columns) {

					if (!column.getIsForeigKey()) {
						w.append("\tpublic " + getDataTypeJava(this.server, column.getDataType()) + " get"
								+ formatText(column.getName(), true) + "(){\n");
						w.append("\t\treturn " + formatText(column.getName(), false) + ";\n");
						w.append("\t}\n\n");

						w.append("\tpublic void set" + formatText(column.getName().toLowerCase(), true) + "("
								+ getDataTypeJava(this.server, column.getDataType()) + " "
								+ formatText(column.getName(), false) + "){\n");
						w.append("\t\tthis." + formatText(column.getName(), false) + " = "
								+ formatText(column.getName(), false) + ";\n");
						w.append("\t}\n\n");
					} else {
						String fkName = "";
						if (column.getName().startsWith("id"))
							fkName = column.getName().substring(2);
						else if (column.getName().endsWith("_id") || column.getName().endsWith("Id"))
							fkName = column.getName().replace("_id", "").replace("Id", "");

						String foreignKeyColumn = formatText(fkName, true);
						w.append("\tpublic " + foreignKeyColumn + "Entity" + " get" + foreignKeyColumn + "(){\n");
						w.append("\t\treturn " + formatText(fkName, false) + ";\n");
						w.append("\t}\n\n");

						w.append("\tpublic void set" + foreignKeyColumn + "(" + foreignKeyColumn + "Entity" + " "
								+ formatText(fkName, false) + "){\n");
						w.append("\t\tthis." + formatText(fkName, false) + " = " + formatText(fkName, false) + ";\n");
						w.append("\t}\n\n");
					}
				}

				w.append("}");

				w.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return false;
		}

		return true;
	}

	private boolean generateModel(String packageNameModel, String tableName, List<Column> columns) {
		try {
			List<Column> col = columns.stream().filter(x -> x.getIsPrimaryKey()).toList();

			if (col.size() == 1) {

				String pathModel = packagePath + "\\" + packageNameModel.replace(".", "\\") + "\\"
						+ formatText(tableName, true) // capitalizeText(tableName)
						+ "Model.java";
				File f = new File(pathModel);
				if (f.exists()) {
					printLog("");
					return true;
				} else if (columns == null)
					columns = jdbcManager.getColumnsByTable(databaseName, tableName);

				f.createNewFile();
				Writer w = new OutputStreamWriter(new FileOutputStream(f));

				w.append("package " + packageNameModel + ";\n\n");

				w.append("import lombok.Getter;\n");
				w.append("import lombok.Setter;\n");

				w.append("@Getter\n");
				w.append("@Setter\n");
				w.append("public class " + formatText(tableName, true) + "Model { \n\n");

				// Add properties
				for (Column column : columns) {
					if (!column.getIsForeigKey()) {
//						if (column.getIsPrimaryKey()) {
//							w.append("\t@Id\n");
//							w.append("\t@GeneratedValue(strategy= GenerationType.IDENTITY)\n");
//						}
////						if (column.getName().equals("housingLocation_id"))
////							printLog("");
//						w.append("\t@Column(name = \"" + column.getName() + "\")\n");
						w.append("\tprivate " + getDataTypeJava(this.server, column.getDataType()) + " "
								+ formatText(column.getName(), false) + ";\n\n");
					} else {

						String fkName = "";
						if (column.getName().startsWith("id"))
							fkName = column.getName().substring(2);
						else if (column.getName().endsWith("_id") || column.getName().endsWith("Id"))
							fkName = column.getName().replace("_id", "").replace("Id", "");
						String foreignKeyColumn = formatText(fkName, true) + "";
//						w.append("\t@ManyToOne\n");
						w.append("\tprivate " + foreignKeyColumn + "Model " + formatText(fkName, false) + ";\n\n");
					}
				}

				// Add constructor

				w.append("\tpublic " + formatText(tableName, true) + "Model (){\n");
				w.append("\t}\n\n");

				w.append("}");

				w.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return false;
		}

		return true;
	}

	private boolean generateDTO(String packageNameDTO, String tableName, List<Column> columns) {
		try {
			List<Column> col = columns.stream().filter(x -> x.getIsPrimaryKey()).toList();

			if (col.size() == 1) {

				String pathModel = packagePath + "\\" + packageNameDTO.replace(".", "\\") + "\\"
						+ formatText(tableName, true) // capitalizeText(tableName)
						+ "DTO.java";
				File f = new File(pathModel);
				if (f.exists()) {
					printLog("");
					return true;
				} else if (columns == null)
					columns = jdbcManager.getColumnsByTable(databaseName, tableName);

				f.createNewFile();
				Writer w = new OutputStreamWriter(new FileOutputStream(f));

				w.append("package " + packageNameDTO + ";\n\n");

				w.append("import lombok.Getter;\n");
				w.append("import lombok.Setter;\n");

				w.append("@Getter\n");
				w.append("@Setter\n");
				w.append("public class " + formatText(tableName, true) + "DTO { \n\n");

				// Add properties
				for (Column column : columns) {
					if (!column.getIsForeigKey()) {

						w.append("\tprivate " + getDataTypeJava(this.server, column.getDataType()) + " "
								+ formatText(column.getName(), false) + ";\n\n");
					} else {

						String fkName = "";
						if (column.getName().startsWith("id"))
							fkName = column.getName().substring(2);
						else if (column.getName().endsWith("_id") || column.getName().endsWith("Id"))
							fkName = column.getName().replace("_id", "").replace("Id", "");
						String foreignKeyColumn = formatText(fkName, true) + "";
						w.append("\tprivate " + foreignKeyColumn + "DTO " + formatText(fkName, false) + ";\n\n");
					}
				}

				// Add constructor

				w.append("\tpublic " + formatText(tableName, true) + "DTO (){\n");
				w.append("\t}\n\n");

				w.append("}");

				w.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return false;
		}

		return true;
	}

	private boolean generateRepository(String packageNameEntity, String packageNameRepository, String tableName) {
		try {
			String pathModel = packagePath + "\\" + packageNameRepository.replace(".", "\\") + "\\"
					+ formatText(tableName, true) + "Repository.java";
			File f = new File(pathModel);
			f.createNewFile();
			Writer w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageNameRepository + ";\n\n");
			w.append("import " + packageName + ".util.IBase;\n\n");
			if (tableName.equalsIgnoreCase("Usuario"))
				w.append("import " + packageNameEntity + "." + formatText(tableName, true) + "Entity;\n");
			w.append("public interface " + formatText(tableName, true) + "Repository extends IBase { \n\n");

			if (tableName.equalsIgnoreCase("Usuario"))
				w.append("\tpublic UsuarioEntity findByUserName(String userName);");

			w.append("}");
			w.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean generateRepositoryImpl(String packageNameEntity, String packageNameRepository,
			String packageNameRepositoryImpl, String tableName) {
		try {
			String pathModel = packagePath + "\\" + packageNameRepositoryImpl.replace(".", "\\") + "\\"
					+ formatText(tableName, true) + "RepositoryImpl.java";
			File f = new File(pathModel);
			f.createNewFile();
			Writer w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageNameRepositoryImpl + ";\n\n");

			w.append("import org.springframework.stereotype.Repository;\n");
			w.append("import " + packageNameEntity + "." + formatText(tableName, true) + "Entity;\n");
			w.append("import " + packageNameRepository + "." + formatText(tableName, true) + "Repository;\n");
			w.append("import " + packageName + ".util.BaseRepository;\n\n");
			if (tableName.equalsIgnoreCase("Usuario")) {
				w.append("import java.util.ArrayList;\r\n"
						+ "import java.util.List;\r\n"
						+ "import org.hibernate.Session;\n"
						+ "import jakarta.persistence.EntityManager;\r\n"
						+ "import jakarta.persistence.criteria.CriteriaBuilder;\r\n"
						+ "import jakarta.persistence.criteria.CriteriaQuery;\r\n"
						+ "import jakarta.persistence.criteria.Predicate;\r\n"
						+ "import jakarta.persistence.criteria.Root;\n");
			}
			

			w.append("@Repository\n");
			w.append("public class " + formatText(tableName, true) + "RepositoryImpl extends BaseRepository<"
					+ formatText(tableName, true) + "Entity> implements " + formatText(tableName, true)
					+ "Repository { \n\n");

			if (tableName.equalsIgnoreCase("Usuario"))
				w.append("	@Override\r\n"
						+ "	public UsuarioEntity findByUserName(String userName) {\r\n"
						+ "		\r\n"
						+ "		Session session = this.getSf().getCurrentSession();\r\n"
						+ "		EntityManager em = session.getEntityManagerFactory().createEntityManager();\r\n"
						+ "		CriteriaBuilder cb = em.getCriteriaBuilder();\r\n"
						+ "		CriteriaQuery<UsuarioEntity> q = cb.createQuery(UsuarioEntity.class);\r\n"
						+ "		Root<UsuarioEntity> root = q.from(UsuarioEntity.class);		\r\n"
						+ "	    List<Predicate> predicates = new ArrayList<>();\r\n"
						+ "	    \r\n"
						+ "	        predicates.add(cb.equal(root.get(\"usuario\"), userName));\r\n"
						+ "		\r\n"
						+ "		q.select(root).where(predicates.toArray(new Predicate[0]));\r\n"
						+ "	\r\n"
						+ "		List<UsuarioEntity> l = em.createQuery(q).getResultList();\r\n"
						+ "		return l.get(0);\r\n"
						+ "	}");
			w.append("}");
			w.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean generateService(String packageNameEntity, String packageNameService, String tableName) {

		try {

			String pathModel = packagePath + "\\" + packageNameService.replace(".", "\\") + "\\"
					+ formatText(tableName, true) + "Service.java";
			File f = new File(pathModel);
			f.createNewFile();
			Writer w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageNameService + ";\n\n");
			w.append("import " + packageName + ".util.IBase;\n\n");
			w.append("public interface " + formatText(tableName, true) + "Service extends IBase { \n\n");

			w.append("}");
			w.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean generateServiceImpl(String packageNameEntity, String packageNameService,
			String packageNameServiceImpl, String tableName) {

		try {

			String pathModel = packagePath + "\\" + packageNameServiceImpl.replace(".", "\\") + "\\"
					+ formatText(tableName, true) + "ServiceImpl.java";
			File f = new File(pathModel);
			f.createNewFile();
			Writer w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageNameServiceImpl + ";\n\n");

			w.append("import org.springframework.stereotype.Service;\n");
			w.append("import " + packageNameEntity + "." + formatText(tableName, true) + "Entity;\n");
			w.append("import " + packageNameService + "." + formatText(tableName, true) + "Service;\n");
			w.append("import " + packageName + ".util.BaseService;\n\n");

			w.append("@Service\n");
			w.append("public class " + formatText(tableName, true) + "ServiceImpl extends BaseService<"
					+ formatText(tableName, true) + "Entity> implements " + formatText(tableName, true)
					+ "Service { \n\n");
			w.append("}");
			w.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean generateController(String packageNameEntity, String packageNameController, String tableName) {

		try {

			String pathModel = packagePath + "\\" + packageNameController.replace(".", "\\") + "\\"
					+ formatText(tableName, true) + "Controller.java";
			File f = new File(pathModel);
			f.createNewFile();
			Writer w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageNameController + ";\n\n");

			w.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
			w.append("import org.springframework.web.bind.annotation.RestController;\n");
			w.append("import " + packageNameEntity + "." + formatText(tableName, true) + "DTO;\n");
			w.append("import " + packageName + ".util.BaseController;\n\n");

			w.append("@RestController\n");
			w.append("@RequestMapping(path = \"" + tableName + "\")\n");
			w.append("public class " + formatText(tableName, true) + "Controller extends BaseController<"
					+ formatText(tableName, true) + "DTO> { \n\n");
			w.append("}");
			w.close();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void addTablesSpringSecurity(String dataBaseName) {

		jdbcManager.connect(dataBaseName);
		ScriptRunner runner = new ScriptRunner(jdbcManager.getConnection(), addOAuth2, addOAuth2);
		try {
			InputStream is = new FileInputStream(PropertiesReading.folder_codegenerator_util + "/"
					+ jdbcManager.getServer() + ".InicioSpringSecurity.sql");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			Reader readerAux = replaceDBName(reader, dataBaseName);
			runner.runScript(readerAux);
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Reader replaceDBName(BufferedReader reader, String dataBaseName) {

		String line;
		Reader stringReader = null;
		try {
			line = reader.readLine();
			StringBuffer text = new StringBuffer();

			while (line != null) {
				String lineAux = line.replace("[DataBaseName]", dataBaseName);
				text.append(lineAux).append("\n");
				line = reader.readLine();
			}

			stringReader = new StringReader(text.toString());
			return stringReader;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stringReader;
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

	public int getProcessProgress() {
		return processProgress;
	}

	public void setProcessProgress(int processProgress) {
		this.processProgress = processProgress;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
