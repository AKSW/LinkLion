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
import de.linkinglod.db.LinkType;
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

				// if o is not resource, it's maybe only meta data, in the moment this is true for hashMapping
				// better: check for #link
				if (object.isResource()) {
					//TODO how to get name out of the URI?
					
					// which EntityObject is S, P, O in the new Link object
					if (p.equals(propString + LLProp.getString("subjectAttribute"))) {
						
						List<EntityObject> eList = getIdFromDb(EntityObject.class, o);		
						if (eList.isEmpty()) {
							linkSubject = createEntityObject(o);
						}
						else {
							linkSubject = eList.get(0).getIdObject();
						}
						//System.out.println("DBCommunication.saveModel(): linkSubject: " + linkSubject);
					}
					else if (p.equals(propString + LLProp.getString("linkType"))) {
						//TODO performance issue: create local linkType array!?
						List<LinkType> ltList = getIdFromDb(LinkType.class, o);
						if (ltList.isEmpty()) {
							linkPredicate = createLinkType(o);
						}
						else {
							linkPredicate = ltList.get(0).getIdLinkType();
						}
						//System.out.println("DBCommunication.saveModel(): linkPredicate: " + linkPredicate);
					}
					else if (p.equals(propString + LLProp.getString("objectAttribute"))) {
						List<EntityObject> eList = getIdFromDb(EntityObject.class, o);
						if (eList.isEmpty()) {
							linkObject = createEntityObject(o);
						}
						else {
							linkObject = eList.get(0).getIdObject();
						}
						//System.out.println("DBCommunication.saveModel(): linkObject: " + linkObject);
					}
				}
				
				// mapping needs to be created only once (per mapping)
				if (!isMappingCreated) {
					if (p.equals(propString + LLProp.getString("hashMapping"))) {
						//TODO do we need something from the mapping object?
						List<Mapping> mList = getIdFromDb(Mapping.class, o);
						if (mList.isEmpty()) {
							createMapping(o);
							System.out.println("Mapping created");
						}
						isMappingCreated = true;
					}
				}

				if (linkSubject != 0 && linkPredicate != 0 && linkObject != 0) {
					System.out.println("nearly created Link()");
					System.out.println("S: " + linkSubject + " P: " + linkPredicate + " O: " + linkObject);

					List<Link> lList = getIdFromDb(Link.class, s);
					if (lList.isEmpty()) {
						//TODO do we need something from the link object?
						createLink(s, linkSubject, linkPredicate, linkObject);
					}
					linkSubject = 0;
					linkPredicate = 0;
					linkObject = 0;
				}
		}
	}

	/**
	 * Get database session and search for string.
	 * @param <T>
	 * @param target
	 * @return
	 */
	private <T> List<T> getIdFromDb(Class<T> myClass, String target) {

		Session session = InitSessionFactory.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(myClass);
		
		// restrict to column, search for the string target
		if (myClass.equals(EntityObject.class) || myClass.equals(LinkType.class)) {
			criteria.add(Restrictions.eq("uri", target));
		} 
		else if (myClass.equals(Mapping.class)) {
			criteria.add(Restrictions.eq("hashMapping", target));
		}
		else if (myClass.equals(Link.class)) {
			criteria.add(Restrictions.eq("hashLink", target));
		}
		
		return criteria.list();
	}

	/**
	 * Create a new EntityObject with unique ID.
	 * @param uri
	 * @return Unique ID of the newly generated EntityObject
	 */
	private long createEntityObject(String uri) {
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
	private <T> void getSessionAndSave(T hibObject) {
		Session session = InitSessionFactory.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.save(hibObject);
		tx.commit();
	}
	
	/**
	 * Create a new LinkType with unique ID.
	 * @param uri
	 * @return Unique ID of the newly generated LinkType
	 */
	private long createLinkType(String uri) {
		LinkType linkType = new LinkType();
		linkType.setUri(uri);
		
		getSessionAndSave(linkType);

		return linkType.getIdLinkType();
	}
	
	/**
	 * test
	 * @return
	 */
	private User createUser() {
		User testUser = new User();
		
		long idUser = 4;
		testUser.setName("forest honey");
		testUser.setIdUser(idUser);
		getSessionAndSave(testUser);

		return testUser;
	}

	/**
	 * Create a Link object in the database.
	 * @param hash
	 * @param s
	 * @param p
	 * @param o
	 * @return 
	 */
	private String createLink(String hash, long s, long p, long o) {

		Link link = new Link();

		link.setHashLink(hash);
		link.setO1Id(s);
		link.setO2Id(o);
		link.setLinkType(p);
		
		getSessionAndSave(link);
		
		return link.getHashLink();
	}

	/**
	 * Create a Mapping object in the database.
	 * @param hashUrl
	 * @return 
	 * @return
	 */
	private String createMapping(String hashUrl) {
		//Jena getLocalName() not working correct with hash
		//String hash  = hashUrl.substring(hashUrl.lastIndexOf(LLProp.getString("fragmentIdentifier")) + 1);

		Mapping mapping = new Mapping();
		mapping.setHashMapping(hashUrl);
		
		getSessionAndSave(mapping);
		
		return mapping.getHashMapping();
	}

}
