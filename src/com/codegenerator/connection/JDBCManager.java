package com.codegenerator.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.codegenerator.util.Column;
import com.codegenerator.util.PropertiesReading;
import com.codegenerator.util.Result;
import com.codegenerator.util.Table;

public class JDBCManager {

	private String host = "";
	private String port = "";
	private String username = ""; // MySQL credentials
	private String password = "";
	private String server = "";
	private Connection con;

	public JDBCManager(String server, String host, String port, String username, String password) {
		this.setHost(host);
		this.setPort(port);
		this.setUsername(username);
		this.setPassword(password);
		this.setServer(server);

	}

	public Result<?> connect() {
		Result<?> result = new Result<>();
		
		String url = "jdbc:"; // table details

		try {
			String prop = getServer().trim() + ".datasource.driver-class-name";

			String driver = PropertiesReading.getProperty(prop);
			StringBuilder urlDB = new StringBuilder(PropertiesReading.getProperty(getServer() + ".datasource.url"));

			url = urlDB.toString().replace("?1", getHost()).replace("?2", getPort());

			Class.forName(driver);
			// Driver name
			setConnection(DriverManager.getConnection(url, getUsername(), getPassword()));

			result.setSuccess(true);
			result.setMessage("Connection successfull !!!!");			
			System.out.println("Connection successfull !!!!");			
			return result;

		} catch (ClassNotFoundException | SQLException e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	public Result<?> connect(String database) {
		Result<?> result = new Result<>();
		
		String url = "jdbc:";

		try {
			String prop = getServer().trim() + ".datasource.driver-class-name";

			String driver = PropertiesReading.getProperty(prop);
			StringBuilder urlDB = new StringBuilder(PropertiesReading.getProperty(getServer() + ".datasource.url.databasename"));

			url = urlDB.toString().replace("?1", getHost()).replace("?2", getPort()).replace("?3", database);

			Class.forName(driver);
			// Driver name
			setConnection(DriverManager.getConnection(url, getUsername(), getPassword()));

			result.setSuccess(true);
			result.setMessage("Connection successfull to DB: "+database+" !!!!");			
			System.out.println("Connection successfull to DB: "+database+" !!!!");			
			return result;

		} catch (ClassNotFoundException | SQLException e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	public List<String> getDataBases() {

		List<String> dbs = new ArrayList<String>();
		Statement st;
		try {
			st = getConnection().createStatement();
			ResultSet rs = null;

			String query = PropertiesReading.getProperty(getServer() + ".query.database");

			rs = st.executeQuery(query);

			while (rs.next()) {
				String name = rs.getString("Database"); // Retrieve name from db
				dbs.add(name);
			}

			st.close(); // close statement
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dbs;
	}
	
	public List<Table> getTableWithSchemaFromDataBase(String database) {
		List<Table> tables = new ArrayList<Table>();
		Statement st;
		try {

			String query = PropertiesReading.getProperty(getServer() + ".query.tables");

			query = query.replace("?1", database);
//			if (server.equals("MySQL")) {
//				query = "SELECT * FROM information_schema.tables " + "WHERE table_schema = '" + database + "'";
//
//			} else {
//
//				query = "use " + database + ";  SELECT * FROM information_schema.tables; ";
//			}
			st = getConnection().createStatement();
			ResultSet rs = st.executeQuery(query); // Execute query
			while (rs.next()) {
				Table table = new Table();
				
				String schema = rs.getString("table_schema"); // Retrieve schema from db
				table.setSchema(schema);				
				String name = rs.getString("table_name"); // Retrieve name from db				
				table.setName(name);
				tables.add(table);
			}

			st.close(); // close statement
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tables;
	}


	public List<String> getTableFromDataBase(String database) {
		List<String> tables = new ArrayList<String>();
		Statement st;
		try {

			String query = PropertiesReading.getProperty(getServer() + ".query.tables");

			query = query.replace("?1", database);
//			if (server.equals("MySQL")) {
//				query = "SELECT * FROM information_schema.tables " + "WHERE table_schema = '" + database + "'";
//
//			} else {
//
//				query = "use " + database + ";  SELECT * FROM information_schema.tables; ";
//			}
			st = getConnection().createStatement();
			ResultSet rs = st.executeQuery(query); // Execute query
			while (rs.next()) {
				String name = rs.getString("table_name"); // Retrieve name from db
				tables.add(name);
			}

			st.close(); // close statement
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tables;
	}

	public List<Column> getColumnsByTable(String database, String tableName) {
		List<Column> columns = new ArrayList<Column>();
		Statement st;
		try {
			String query = PropertiesReading.getProperty(getServer() + ".query.columns");
			query = query.replace("?1", database);
			query = query.replace("?2", tableName);
			st = getConnection().createStatement();
			ResultSet rs = st.executeQuery(query); 
			while (rs.next()) {
				Column column = new Column();
				column.setName(rs.getString("column_name"));
				column.setDataType(rs.getString("data_type"));
				column.setIsNullable(rs.getString("is_nullable").equals("YES"));
				column.setLength(rs.getInt("CHARACTER_MAXIMUM_LENGTH"));
				if (rs.getString("column_key") != null) {
					column.setIsPrimaryKey(rs.getString("column_key").equals("PRI"));
					column.setIsForeignKey(rs.getString("column_key").equals("MUL"));
				}
				
//				column.setName(rs.getString("column_name"));
//				column.setDataType(rs.getString("data_type"));
//				column.setIsNullable(rs.getBoolean("is_nullable"));	
//				column.setIsNullable(rs.getBoolean("AutoIncrement"));
//				column.setIsPrimaryKey(rs.getBoolean("IsPrimaryKey"));
//				column.setIsForeignKey(rs.getBoolean("IsForeignKey"));
				column.setTableReference(rs.getString("REFERENCED_TABLE"));
				columns.add(column);
			}
			st.close(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columns;
	}

	public Connection getConnection() {
		return con;
	}

	public void setConnection(Connection con) {
		this.con = con;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

}
