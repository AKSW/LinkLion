package de.linkinglod.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import de.linkinglod.model.User;

/**
 * @author markus
 * Connection to remote mysql database needs access for the machine from where access is requested! 
 * Better seems: Only localhost access to database! --> testing on localhost
 */
public class DBCommunication {
	
	private static Logger log = LoggerFactory.getLogger(DBCommunication.class);
	private static String user = LinkingLodProperties.getString("DBCommunication.user");
	private static String password = LinkingLodProperties.getString("DBCommunication.password");
	private static String server = LinkingLodProperties.getString("DBCommunication.server");
	private static String localServer = LinkingLodProperties.getString("DBCommunication.localServer");
	
	private MysqlDataSource dataSource = null;

	/**
	 * Creates, maintains a database connection to MySQL database.
	 * TODO support local/remote db connection
	 */
	public DBCommunication() {
	}
	
	/**
	 * Temp main.
	 * @param args
	 */
	public static void main(String[] args) {
	      try {
	    	  // test connection
	    	  Class.forName("com.mysql.jdbc.Driver");
	    	  Connection conn = null;
	    	  //conn = DriverManager.getConnection(server, user, password);
	    	  conn = DriverManager.getConnection(localServer, user, password);
	    	  conn.close();
	    	  
	    	  // work:
	    	  createUser();
	    	  
	      } catch (RuntimeException e) {
	          try {
	             Session session = InitSessionFactory.getInstance().getCurrentSession();
	             if (session.getTransaction().isActive())
	                session.getTransaction().rollback();
	          } catch (HibernateException e1) {
	             log.error("Error rolling back transaction");
	          }
	          throw e;
	       } catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void loadJDBCDriver() {
		// http://stackoverflow.com/questions/2839321/java-connectivity-with-mysql
		dataSource = new MysqlDataSource();
		// TODO set db properties
	}

	private static User createUser() {
		User testUser = new User();
		
		long idUser = 4;
		testUser.setName("forest honey");
		testUser.setIdUser(idUser);

		Session session = InitSessionFactory.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.save(testUser);
		tx.commit();
		return testUser;
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
		jenaModel.write(System.out, "N-TRIPLE");
		List<RDFNode> jenaList = jenaModel.createList().asJavaList();
		System.out.println("jenaList.count() " + jenaList.size());
		for (RDFNode node: jenaList) {
			System.out.println("node.asLiteral().toString(): " + node.asLiteral().toString());
		}
	}
	
}
