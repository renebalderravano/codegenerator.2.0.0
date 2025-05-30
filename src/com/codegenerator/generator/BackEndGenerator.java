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
import java.util.List;
import java.util.Set;

import com.codegenerator.connection.JDBCManager;
import com.codegenerator.util.Column;
import com.codegenerator.util.DataTypeConverter;
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
	
	private int processProgress = 0;
	
	boolean addOAuth2;

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
			
			printLog("Creando Directorio para backend..");
			
			FileManager.createRootDirectory(workspace, projectName);
			FileManager.createPackage(this.packagePath, this.packageName);
			FileManager.createPackage(this.packagePath, this.packageName + ".configuration");
			FileManager.createPackage(this.packagePath, this.packageName + ".model");
			FileManager.createPackage(this.packagePath, this.packageName + ".repository.impl");
			FileManager.createPackage(this.packagePath, this.packageName + ".service.impl");
			FileManager.createPackage(this.packagePath, this.packageName + ".controller");
			setProcessProgress(30);
			for (Object[] table : tables) {
				String tableName = (String) table[0];
				printLog("Obteniendo columnas de la tabla " +tableName+"");
				List<Column> columns = jdbcManager.getColumnsByTable(databaseName, tableName);
				Table tbl = new Table();
				tbl.setName(tableName);
				tbl.setColumns(columns);
				printLog("Generando modelo de la tabla " +tableName+"");
				generateModel(tableName, columns);
				printLog("Generando repositorio de la tabla " +tableName+"");
				generateRepository(tableName);
				printLog("Generando servicio de la tabla " +tableName+"");
				generateService(tableName);
				printLog("Generando controlador de la tabla " +tableName+"");
				generateController(tableName);
			}
			
			setProcessProgress(50);

			printLog("Preparando carpeta util...");
			// Preparar carpteta util
			FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/util",
					packagePath + "\\" + packageName.replace(".", "\\") + "\\util", false);

			FileManager.replaceTextInFilesFolder(packagePath + "\\" + packageName.replace(".", "\\") + "\\util",
					"[packageName]", packageName);

			printLog("Preparando clase principal de spring Boot Application.java...");
			// Preparar clase principal de spring Boot Application.java
			FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/Application.java",
					packagePath + "\\" + packageName.replace(".", "\\") + "\\Application.java", false);

			FileManager.replaceTextInFile(packagePath + "\\" + packageName.replace(".", "\\") + "\\Application.java",
					"[packageName]", packageName);
			setProcessProgress(60);
			printLog("Preparando configuración hibernate...");
			// preparar configuracion Hibernate

			FileManager.copyDir(
					PropertiesReading.folder_codegenerator_util + "/configuration/HibernateConfiguration.java",
					packagePath + "\\" + packageName.replace(".", "\\")
							+ "\\configuration\\HibernateConfiguration.java",
					false);

//			FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/configuration/WebConfiguration.java",
//					packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration\\WebConfiguration.java",
//					false);

			FileManager.replaceTextInFilesFolder(
					packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration", "[packageName]",
					packageName);
			setProcessProgress(70);
			if (this.addOAuth2) {

				printLog("Agregando Spring Security Oauth2...");
				FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/security/pom.xml",
						workspace + "\\" + projectName + "\\pom.xml", false);

				FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/security/configuration",
						packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration", false);

				FileManager.replaceTextInFilesFolder(
						packagePath + "\\" + packageName.replace(".", "\\") + "\\configuration", "[packageName]",
						packageName);

				printLog("\tCreando tablas requeridas por Spring Security Oauth2...");
				addTablesSpringSecurity(databaseName);

				printLog("\tCreando modelo user para Spring Security Oauth2...");
				FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/security/model",
						packagePath + "\\" + packageName.replace(".", "\\") + "\\model", false);

				FileManager.replaceTextInFilesFolder(packagePath + "\\" + packageName.replace(".", "\\") + "\\model",
						"[packageName]", packageName);

				setProcessProgress(75);
				printLog("\tCreando repository user para Spring Security Oauth2...");
				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util + "/security/repository/UserRepository.java",
						packagePath + "\\" + packageName.replace(".", "\\") + "\\repository\\UserRepository.java",
						false);

				FileManager.replaceTextInFilesFolder(
						packagePath + "\\" + packageName.replace(".", "\\") + "\\repository\\UserRepository.java",
						"[packageName]", packageName);

				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util
								+ "/security/repository/impl/UserRepositoryImpl.java",
						packagePath + "\\" + packageName.replace(".", "\\")
								+ "\\repository\\impl\\UserRepositoryImpl.java",
						false);

				FileManager.replaceTextInFilesFolder(packagePath + "\\" + packageName.replace(".", "\\")
						+ "\\repository\\impl\\UserRepositoryImpl.java", "[packageName]", packageName);
				printLog("\tCreando service user para Spring Security Oauth2...");
				generateService("User");
				printLog("\tCreando repository authority para Spring Security Oauth2...");
				generateRepository("Authority");
				generateService("Authority");

				printLog("\tCreando UserDetailsService para Spring Security Oauth2...");
				FileManager.copyDir(
						PropertiesReading.folder_codegenerator_util + "/UserDetailsServiceImpl.java", packagePath + "\\"
								+ packageName.replace(".", "\\") + "\\service\\impl\\UserDetailsServiceImpl.java",
						false);

				FileManager.replaceTextInFile(packagePath + "\\" + packageName.replace(".", "\\")
						+ "\\service\\impl\\UserDetailsServiceImpl.java", "[packageName]", packageName);
			} else
				FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/pom.xml",
						workspace + "\\" + projectName + "\\pom.xml", false);
			
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
			FileManager.copyDir(PropertiesReading.folder_codegenerator_util + "/resources", resourcesPath, false);

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
	
	private boolean generateModel(String tableName, List<Column> columns) {

		try {

			List<Column> col = columns.stream().filter(x -> x.getIsPrimaryKey()).toList();

			if (col.size() == 1) {

				String pathModel = packagePath + "\\" + packageName.replace(".", "\\") + "\\model\\"
						+ formatText(tableName, true) // capitalizeText(tableName)
						+ ".java";
				File f = new File(pathModel);				
				if (f.exists()) {
					printLog("");
					return true;
				} else if (columns == null)
					columns = jdbcManager.getColumnsByTable(databaseName, tableName);

				f.createNewFile();
				Writer w = new OutputStreamWriter(new FileOutputStream(f));

				w.append("package " + packageName + ".model;\n\n");

				w.append("import javax.persistence.*;\n");

				w.append("@Entity\n");
				w.append("@Table(name = \"" + tableName + "\")\n");
				w.append("public class " + formatText(tableName, true) + "{ \n\n");

				// Add properties
				for (Column column : columns) {
					if (!column.getIsForeigKey()) {
						if (column.getIsPrimaryKey()) {
							w.append("\t@Id\n");
							w.append("\t@GeneratedValue(strategy= GenerationType.IDENTITY)\n");
						}
//						if (column.getName().equals("housingLocation_id"))
//							printLog("");
						w.append("\t@Column(name = \"" + column.getName() + "\")\n");
						w.append("\tprivate " + getDataTypeJava(this.server, column.getDataType()) + " "
								+ formatText(column.getName(), false) + ";\n\n");
					} else {

						String foreignKeyColumn = formatText(column.getName().replace("_id", ""), true);
						w.append("\t@ManyToOne\n");
						w.append("\tprivate " + foreignKeyColumn + " " + foreignKeyColumn.toLowerCase() + ";\n\n");
					}
				}

				// Add constructor

				w.append("\tpublic " + formatText(tableName, true) + "(){\n");
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
						String foreignKeyColumn = formatText(column.getName().replace("_id", ""), true);
						w.append("\tpublic " + foreignKeyColumn + " get" + foreignKeyColumn + "(){\n");
						w.append("\t\treturn " + foreignKeyColumn.toLowerCase() + ";\n");
						w.append("\t}\n\n");

						w.append("\tpublic void set" + foreignKeyColumn + "(" + foreignKeyColumn + " "
								+ foreignKeyColumn.toLowerCase() + "){\n");
						w.append("\t\tthis." + foreignKeyColumn.toLowerCase() + " = " + foreignKeyColumn.toLowerCase()
								+ ";\n");
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
	
	private boolean generateRepository(String tableName) {
		try {
			String pathModel = packagePath + "\\" + packageName.replace(".", "\\") + "\\repository\\"
					+ formatText(tableName, true) + "Repository.java";
			File f = new File(pathModel);
			f.createNewFile();
			Writer w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageName + ".repository;\n\n");
			w.append("import " + packageName + ".model." + capitalizeText(tableName) + ";\n");
			w.append("import " + packageName + ".util.IBase;\n\n");
			w.append("public interface " + formatText(tableName, true) + "Repository extends IBase<"
					+ formatText(tableName, true) + ">{ \n\n");
			w.append("}");
			w.close();

			pathModel = packagePath + "\\" + packageName.replace(".", "\\") + "\\repository\\impl\\"
					+ formatText(tableName, true) + "RepositoryImpl.java";
			f = new File(pathModel);
			f.createNewFile();
			w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageName + ".repository.impl;\n\n");

			w.append("import org.springframework.stereotype.Repository;\n");
			w.append("import " + packageName + ".model." + formatText(tableName, true) + ";\n");
			w.append("import " + packageName + ".repository." + formatText(tableName, true) + "Repository;\n");
			w.append("import " + packageName + ".util.BaseRepository;\n\n");

			w.append("@Repository\n");
			w.append("public class " + formatText(tableName, true) + "RepositoryImpl extends BaseRepository<"
					+ formatText(tableName, true) + "> implements " + formatText(tableName, true)
					+ "Repository { \n\n");

			w.append("}");
			w.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	private boolean generateService(String tableName) {

		try {

			String pathModel = packagePath + "\\" + packageName.replace(".", "\\") + "\\service\\"
					+ formatText(tableName, true) + "Service.java";
			File f = new File(pathModel);
			f.createNewFile();
			Writer w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageName + ".service;\n\n");
			w.append("import " + packageName + ".model." + formatText(tableName, true) + ";\n");
			w.append("import " + packageName + ".util.IBase;\n\n");
			w.append("public interface " + formatText(tableName, true) + "Service extends IBase<"
					+ formatText(tableName, true) + ">{ \n\n");

			w.append("}");
			w.close();

			pathModel = packagePath + "\\" + packageName.replace(".", "\\") + "\\service\\impl\\"
					+ formatText(tableName, true) + "ServiceImpl.java";
			f = new File(pathModel);
			f.createNewFile();
			w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageName + ".service.impl;\n\n");

			w.append("import org.springframework.stereotype.Service;\n");
			w.append("import " + packageName + ".model." + formatText(tableName, true) + ";\n");
			w.append("import " + packageName + ".service." + formatText(tableName, true) + "Service;\n");
			w.append("import " + packageName + ".util.BaseService;\n\n");

			w.append("@Service\n");
			w.append("public class " + formatText(tableName, true) + "ServiceImpl extends BaseService<"
					+ formatText(tableName, true) + "> implements " + formatText(tableName, true) + "Service { \n\n");
			w.append("}");
			w.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	private boolean generateController(String tableName) {

		try {

			String pathModel = packagePath + "\\" + packageName.replace(".", "\\") + "\\controller\\"
					+ formatText(tableName, true) + "Controller.java";
			File f = new File(pathModel);
			f.createNewFile();
			Writer w = new OutputStreamWriter(new FileOutputStream(f));

			w.append("package " + packageName + ".controller;\n\n");

			w.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
			w.append("import org.springframework.web.bind.annotation.RestController;\n");
			w.append("import " + packageName + ".model." + formatText(tableName, true) + ";\n");
			w.append("import " + packageName + ".util.BaseController;\n\n");

			w.append("@RestController\n");
			w.append("@RequestMapping(path = \"" + tableName + "\")\n");
			w.append("public class " + formatText(tableName, true) + "Controller extends BaseController<"
					+ formatText(tableName, true) + "> { \n\n");
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
