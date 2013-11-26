package de.linkinglod.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import de.linkinglod.db.EntityObject;
import de.linkinglod.db.Link;
import de.linkinglod.db.Linktype;
import de.linkinglod.db.Mapping;
import de.linkinglod.db.User;

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

	public void obtainConnection() throws SQLException {
//		Connection conn = dataSource.getConnection();
		//Statement stmt = conn.createStatement();
		
		
//		ResultSet rs = stmt.executeQuery("SELECT ID FROM USERS");
//		// 	TODO
//		rs.close();
//		stmt.close();
//		conn.close();
	}

	/**
	 * TODO Is Jena Model good to work with here?
	 * @param dbModel
	 */
	public void saveModel(Model jenaModel) {
		
		System.out.println("saveModel(): jenaModel.isEmpty(): " + jenaModel.isEmpty());
		
		StmtIterator modelIterator = jenaModel.listStatements();
		List<Statement> listModel = modelIterator.toList();
		
		long linkSubject = 0;
		long linkPredicate = 0; 
		long linkObject = 0;
		boolean isMappingCreated = false;

		
		String ns = LLProp.getString("ns");
		String lim = LLProp.getString("delimiter");
		String vocProp = LLProp.getString("vocabularyProperty");
		String propString = ns + lim + vocProp + lim;

		// TODO adapt if there are not only link objects in here
		for (Statement statement: listModel) {

				// S, P, O of single triple
				Resource subject = statement.getSubject();     
				Property predicate = statement.getPredicate(); 
				RDFNode object = statement.getObject();
				String s = subject.toString();
				String p = predicate.toString();
				String o = object.toString();

				// if o is not ressource, it's maybe only metadata, in the moment this is true for hashMapping
				// better: check for #link
				if (object.isResource()) {

					//TODO how to check if object is already existent
					//TODO o is not always EntityObject!					
					//TODO how to get name out of the URI?
					
					// which EntityObject is S, P, O in the new Link object
					// TODO extract to method?
					if (p.equals(propString + LLProp.getString("subjectAttribute"))) {
						if (!existsInDb(EntityObject.class, o)) {
							// o1Id;
							linkSubject = createEntityObject(o);
						}
					} 
					else if (p.equals(propString + LLProp.getString("linkType"))) {
						//TODO performance issue: create local linktype array!?
						if (!existsInDb(Linktype.class, o)) {
							// linkType;
							linkPredicate = createLinktype(o);
						}
					} 
					else if (p.equals(propString + LLProp.getString("objectAttribute"))) {
						if (!existsInDb(EntityObject.class, o)) {
							// o2Id;
							linkObject = createEntityObject(o);
						}
					}
				}
				
				// mapping needs to be created only once
				// hashMapping;
				//TODO not working here!!!

				if (!isMappingCreated) {
					if (p.equals(propString + LLProp.getString("hashMapping"))) {
						//TODO extract hash
						if (!existsInDb(Mapping.class, o)) {
							createMapping(o);
						}
						isMappingCreated = true;
					}
				}

//				hashLink;
				if (linkSubject != 0 && linkPredicate != 0 && linkObject != 0) {
					//TODO extract hash
					if (!existsInDb(Link.class, o)) {
						createLink(s, linkSubject, linkPredicate, linkObject);
					}
				}
		}
	}

	/**
	 * Get database session and search for string.
	 * TODO Add (generic) search objects.
	 * @param <T> only working for EntityObject and Linktype!!
	 * @param o
	 * @return
	 */
	private <T> boolean existsInDb(Class<T> myClass, String o) {

		Session session = InitSessionFactory.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(myClass);
		
		// restrict to column uri, search for the string o
		criteria.add(Restrictions.eq("uri", o));
		List<T> result = criteria.list();
		//System.out.println("getSessionAndSearch(): result.size(): " + result.size());
		
		return !result.isEmpty();
	}

	/**
	 * Create a new EntityObject with unique ID.
	 * @param uri
	 * @return Unique ID of the newly generated EntityObject
	 */
	private static long createEntityObject(String uri) {
		EntityObject eo = new EntityObject();
		eo.setUri(uri);
		
		getSessionAndSave(eo);

		//TODO is eo.getIdObjet already the correct value?
		return eo.getIdObject();
	}

	/**
	 * Establish a session to the database and save the (generic) hibernate object.
	 * @param hibObject
	 */
	private static <T> void getSessionAndSave(T hibObject) {
		Session session = InitSessionFactory.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.save(hibObject);
		tx.commit();
	}
	
	/**
	 * Create a new Linktype with unique ID.
	 * @param uri
	 * @return Unique ID of the newly generated Linktype
	 */
	private static long createLinktype(String uri) {
		Linktype linktype = new Linktype();
		linktype.setUri(uri);
		
		getSessionAndSave(linktype);

		return linktype.getIdLinktype();
	}
	
	/**
	 * test
	 * @return
	 */
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
	 * Create a Link object in the database.
	 * @param hash
	 * @param s
	 * @param p
	 * @param o
	 */
	private void createLink(String hash, long s, long p, long o) {

		Link link = new Link();

		link.setHashLink(hash);
		link.setO1Id(s);
		link.setO2Id(o);
		link.setLinkType(p);
	}

	/**
	 * Create a Mapping object in the database.
	 * @param hash
	 * @return
	 */
	private String createMapping(String hash) {

		Mapping mapping = new Mapping();

		mapping.setHashMapping(hash);

		return hash;
	}

}
