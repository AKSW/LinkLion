package de.linkinglod.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

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

import de.linkinglod.db.Algorithm;
import de.linkinglod.db.EntityObject;
import de.linkinglod.db.Link;
import de.linkinglod.db.LinkType;
import de.linkinglod.db.Mapping;
import de.linkinglod.db.Source;
import de.linkinglod.db.User;
import de.linkinglod.io.Writer;

/**
 * @author markus
 * Connection to remote mysql database needs access for the machine from where access is requested! 
 * Better seems: Only localhost access to database! --> testing on localhost
 */
public class DBCommunication implements Writer {
	
	private static Logger log = LoggerFactory.getLogger(DBCommunication.class);
	private static String user = LLProp.getString("DBCommunication.user");
	private static String password = LLProp.getString("DBCommunication.password");
	private static String server = LLProp.getString("DBCommunication.server");
	private static String localServer = LLProp.getString("DBCommunication.localServer");
	
	private Model ontoModel = OntologyLoader.getOntModel();

	// load namespaces
	private String rdf = ontoModel.getNsPrefixURI("rdf");
	private String prov = ontoModel.getNsPrefixURI("prov");
	private String llont = ontoModel.getNsPrefixURI("llont");
	private String llalg = ontoModel.getNsPrefixURI("llalg");
	private String lldat = ontoModel.getNsPrefixURI("lldat");

	// load properties
	private Property propSubject = ontoModel.getProperty(rdf + "subject");
	private Property propPredicate = ontoModel.getProperty(rdf + "predicate");
	private Property propObject = ontoModel.getProperty(rdf + "object");
	private Property wasDerivedFrom = ontoModel.getProperty(prov + "wasDerivedFrom");
	private Property generatedAtTime = ontoModel.getProperty(prov + "generatedAtTime");
	private Property wasGeneratedBy = ontoModel.getProperty(prov + "wasGeneratedBy");
	private Property hasSource = ontoModel.getProperty(llont + "hasSource");
	private Property hasTarget = ontoModel.getProperty(llont + "hasTarget");
	
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

	/**
	 * @param dbModel
	 */
	@Override
	public void write(String graph, Model jenaModel) {

		if (jenaModel.isEmpty()) {
			System.out.println("saveModel(): jenaModel.isEmpty(): " + jenaModel.isEmpty());
			return;
		}

		long linkSubject = 0;
		long linkPredicate = 0; 
		long linkObject = 0;
		boolean isMappingCreated = false;
		String hashMappingUrl = "";

		StmtIterator modelIterator = jenaModel.listStatements();
		List<Statement> listModel = modelIterator.toList();

		for (Statement statement: listModel) {

			// S, P, O of single triple
			Resource subject = statement.getSubject();     
			Property predicate = statement.getPredicate(); 
			RDFNode object = statement.getObject();
			String s = subject.toString();
			String p = predicate.toString();
			String o = object.toString();

			// isMappingCreated ensures no unneeded DB query at this point
			if (!isMappingCreated) {
				if (p.equals(wasDerivedFrom.toString())) {
					List<Mapping> mList = getIdFromDb(Mapping.class, o);
					
					if (mList.isEmpty()) {
						hashMappingUrl = createMapping(o);
					}
					isMappingCreated = true;
				}
			}

			// which EntityObject is S, P, O in the new Link object
			if (p.equals(propSubject.toString())) {
				List<EntityObject> eList = getIdFromDb(EntityObject.class, o);	
				
				if (eList.isEmpty()) {
					linkSubject = createEntityObject(o);
				}
				else {
					linkSubject = eList.get(0).getIdObject();
				}
				//System.out.println("DBCommunication.saveModel(): linkSubject: " + linkSubject);
			}
			else if (p.equals(propPredicate.toString())) {
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
			else if (p.equals(propObject.toString())) {
				List<EntityObject> eList = getIdFromDb(EntityObject.class, o);
				
				if (eList.isEmpty()) {
					linkObject = createEntityObject(o);
				}
				else {
					linkObject = eList.get(0).getIdObject();
				}
				//System.out.println("DBCommunication.saveModel(): linkObject: " + linkObject);
			}


			if (linkSubject != 0 && linkPredicate != 0 && linkObject != 0) {
				List<Link> lList = getIdFromDb(Link.class, s);
				
				if (lList.isEmpty()) {
					createLink(s, linkSubject, linkPredicate, linkObject, hashMappingUrl);
				}
				
				linkSubject = 0;
				linkPredicate = 0;
				linkObject = 0;
			}

			if (p.equals(hasSource.toString()) || p.equals(hasTarget.toString())) {
				List<Source> sList = getIdFromDb(Source.class, o);
				
				if (sList.isEmpty()) {
					createSource(o);
					// TODO create MappingHasSource?
				}
			}
			// TODO User
			// TODO Framework
			if (p.equals(wasGeneratedBy.toString())) {
				List<Algorithm> aList = getIdFromDb(Algorithm.class, o);
				
				if (aList.isEmpty()) {
					createAlgorithm(o, hashMappingUrl);
					System.out.println("Algorithm added");
					// TODO create MappingHasSource?
				}
			}
			
			// TODO not working in test, enable later, not EVERY generatedAtTime is valid for the mapping
			if (p.equals(generatedAtTime.toString()) ) { //&& s.equals(hashMappingUrl)) { 
				Session session = InitSessionFactory.getInstance().getCurrentSession();
				Mapping mapping = (Mapping) session.load(Mapping.class, new String(s));	

				String time = convertSeparators(o.substring(0, o.lastIndexOf("^") - 1));
				Timestamp value = Timestamp.valueOf(time);
				
				mapping.setTimeGenerated(value);
				
				getSessionAndSave(mapping);
				System.out.println("Date added");
			}
		}
	}
	
	static String convertSeparators(String input) {
	    char[] chars = input.toCharArray();
	    chars[10] = ' ';
	    chars[13] = ':';
	    chars[16] = ':';
	    return new String(chars);
	}

	/**
	 * Creates an algorithm, url needs to be unique, is NOT good implemented for now!
	 * @param algoUri
	 * @param hashMappingUrl
	 */
	private void createAlgorithm(String algoUri, String hashMappingUrl) {
		// TODO not really unique
		Algorithm algorithm = new Algorithm();
		algorithm.setUri(algoUri);
		algorithm.setHashMapping(hashMappingUrl);
		
		getSessionAndSave(algorithm);
	}

	private void createSource(String uri) {
		Source source = new Source();
		source.setUri(uri);
		
		getSessionAndSave(source);	
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
		if (myClass.equals(EntityObject.class) || myClass.equals(LinkType.class) 
				|| myClass.equals(Source.class) || myClass.equals(Algorithm.class)) {
			criteria.add(Restrictions.eq("uri", target));
		} 
		else if (myClass.equals(Mapping.class)) {
			criteria.add(Restrictions.eq("hashMapping", target));
		}
		else if (myClass.equals(Link.class)) {
			criteria.add(Restrictions.eq("hashLink", target));
		}
		// TODO close session here?
		
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
		
		long idUser = 3;
		testUser.setName("foo bar");
		testUser.setIdUser(idUser);
		getSessionAndSave(testUser);

		return testUser;
	}

	/**
	 * Create a Link object in the database and return the hashLink of the already saved link.
	 * @param hashLink
	 * @param subject subject
	 * @param predicate predicate
	 * @param object object
	 * @param hashMappingUrl 
	 * @return hashLink
	 */
	private String createLink(String hashLink, long subject, long predicate, long object, String hashMappingUrl) {

		Link link = new Link();

		link.setHashLink(hashLink);
		link.setO1Id(subject);
		link.setO2Id(object);
		link.setLinkType(predicate);
		link.setHashMapping(hashMappingUrl);
		
		getSessionAndSave(link);
		
		return link.getHashLink();
	}

	/**
	 * Create a Mapping object in the database.
	 * @param hashUrl
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
