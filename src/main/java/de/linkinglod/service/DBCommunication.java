package de.linkinglod.service;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * @author markus
 *
 */
public class DBCommunication {
	
	private static Logger log = LoggerFactory.getLogger(DBCommunication.class);

	
	MysqlDataSource dataSource = null;

	/**
	 * Creates, maintains a database connection to MySQL database.
	 * TODO support local/remote db connection
	 */
	public DBCommunication() {
		//loadJDBCDriver();
	}
	
	/**
	 * Temp main.
	 * @param args
	 */
	public static void main(String[] args) {
	      try {
	    	  // do stuff
	       } catch (RuntimeException e) {
	          try {
	             Session session = InitSessionFactory.getInstance()
	                   .getCurrentSession();
	             if (session.getTransaction().isActive())
	                session.getTransaction().rollback();
	          } catch (HibernateException e1) {
	             log.error("Error rolling back transaction");
	          }
	          // throw the exception again
	          throw e;
	       }
	}

	private void loadJDBCDriver() {
		// http://stackoverflow.com/questions/2839321/java-connectivity-with-mysql
		// TODO remove user data if published
		dataSource = new MysqlDataSource();
		
		// TODO set db properties

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
	public void saveModel(Model jenaModel) {
		List<RDFNode> jenaList = jenaModel.createList().asJavaList();
		
		for (RDFNode node: jenaList) {
			//TODO
		}
	}
	
}
