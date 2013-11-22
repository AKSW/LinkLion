package de.linkinglod.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import de.linkinglod.model.EntityObject;
import de.linkinglod.model.Link;
import de.linkinglod.model.Mapping;
import de.linkinglod.model.User;

/**
 * @author markus
 * Connection to remote mysql database needs access for the machine from where access is requested! 
 * Better seems: Only localhost access to database! --> testing on localhost
 */
public class DBCommunication {
	
	private static Logger log = LoggerFactory.getLogger(DBCommunication.class);
	private static String user = LLProp.getString("DBCommunication.user");
	private static String password = LLProp.getString("DBCommunication.password");
	private static String server = LLProp.getString("DBCommunication.server");
	private static String localServer = LLProp.getString("DBCommunication.localServer");
	
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
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
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
	    	  Session session = InitSessionFactory.getInstance().getCurrentSession();
	    	  if (session.getTransaction().isActive())
	    		  session.getTransaction().rollback();
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
	
	/**
	 * Create a new EntityObject with unique ID.
	 * TODO Ensure unique ID.
	 * @param uri
	 * @return Unique ID of the newly generated EntityObject
	 */
	private static long createEntityObject(String uri) {
		EntityObject object = new EntityObject();
		object.setUri(uri);
		
		return object.getIdObject();
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
		
		System.out.println("saveModel(): jenaModel.isEmpty(): " + jenaModel.isEmpty());
		
		StmtIterator modelIterator = jenaModel.listStatements();
		List<com.hp.hpl.jena.rdf.model.Statement> listModel = modelIterator.toList();
		
		long linkSubject = 0;
		long linkPredicate = 0; 
		long linkObject = 0;

		// iterate through all triples
		for (com.hp.hpl.jena.rdf.model.Statement statement: listModel) {

				// S, P, O of single triple
				Resource s = statement.getSubject();     
				Property p = statement.getPredicate(); 
				RDFNode o = statement.getObject();
				
				// if o is not ressource, it's maybe only metadata
				if (o.isResource()) {
					//TODO how to check if object is already existent
					//TODO o is not always EntityObject!
					long objectId = createEntityObject(o.toString());
					
					//TODO how to get name out of the URI?
					
					// which EntityObject is S, P, O in the new Link object
					if (p.toString().equals(LLProp.getString("subjectAttribute"))) {
						linkSubject = objectId;
					} else					
					if (p.toString().equals(LLProp.getString("linkType"))) {
						linkPredicate = objectId;
					} else					
					if (p.toString().equals(LLProp.getString("objectAttribute"))) {
						linkObject = objectId;
					}
				}
				
				// TODO only if s, p and o are filled
				if (p.toString().equals(LLProp.getString("hashMapping"))) {
					// TODO only create mapping once
					createMapping(o.toString());
					createLink(s.toString(), linkSubject, linkPredicate, linkObject);
				}
				
//				hashLink;
//				o1Id;
//				o2Id;
//				linkType;
//				similarity;
//				hashMapping;
			System.out.println("s.toString(): " + statement.toString());
		}
	}

	private void createLink(String hash, long s, long p, long o) {
		
		Link link = new Link();
		
		link.setHashLink(hash);
		link.setO1Id(s);
		link.setO2Id(o);
		link.setLinkType(p);
	}

	private String createMapping(String hash) {
		
		Mapping mapping = new Mapping();
		
		mapping.setHashMapping(hash);
		
		return hash;
	}
	
}
