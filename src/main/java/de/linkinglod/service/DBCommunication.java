package de.linkinglod.service;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Statement;

import com.hp.hpl.jena.rdf.model.Model;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;


public class DBCommunication {
	
	MysqlDataSource dataSource = null;

	/**
	 * Creates, maintains a database connection to MySQL database.
	 * TODO support local/remote db connection
	 */
	public DBCommunication() {
		loadJDBCDriver();
	}
	
	/**
	 * Temp main.
	 * @param args
	 */
	public static void main(String[] args) {

	}

	private void loadJDBCDriver() {
		// http://stackoverflow.com/questions/2839321/java-connectivity-with-mysql
		// TODO remove user data if published
		dataSource = new MysqlDataSource();
		dataSource.setUser("nentwig");
		dataSource.setPassword("monskyho");
		dataSource.setServerName("wdiserv1.informatik.uni-leipzig.de");
	}
	
	public void obtainConnection() throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();
		
		
		ResultSet rs = stmt.executeQuery("SELECT ID FROM USERS");
		// 	TODO
		rs.close();
		stmt.close();
		conn.close();
	}

	/**
	 * TODO Is Jena Model good to work with here?
	 * @param dbModel
	 */
	public void saveModel(Model dbModel) {
		// TODO Auto-generated method stub		
	}
	
}
