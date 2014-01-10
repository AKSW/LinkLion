package de.linkinglod.service;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import de.linkinglod.db.Algorithm;
import de.linkinglod.db.MappingHasSource;
import de.linkinglod.db.RDFSResource;
import de.linkinglod.db.Link;
import de.linkinglod.db.LinkType;
import de.linkinglod.db.Mapping;
import de.linkinglod.db.Source;
import de.linkinglod.db.User;
import de.linkinglod.io.Writer;
import de.linkinglod.util.SQLUtils;

/**
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
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
		long objOverall = 0;
		long objAlreadyExisting = 0;
		boolean isSameAs = false;
		long sameAs = 0;
		
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
					try {
						hashMappingUrl = createMapping(o);
						isMappingCreated = true;
					}
					catch (ConstraintViolationException e) {
					}
				}
			}

			// which RDFSResource is S, P, O in the new Link object
			if (p.equals(propSubject.toString())) {
				++objOverall;
				try {
					linkSubject = createRDFSResource(o);
				}
				catch (ConstraintViolationException e) {
					++objAlreadyExisting;
					
					// object is already in DB: get object from DB, return id
					List<RDFSResource> resList = getIdFromDb(RDFSResource.class, o, "uri");
					linkSubject = resList.get(0).getIdResource();

				} // TODO finally block? check if linkObject is still 0?
			}
			else if (p.equals(propPredicate.toString())) {

				if (isSameAs) {
					linkPredicate = sameAs;
				}
				else {
					try {
						linkPredicate = createLinkType(o);
						if (p.equals("http://www.w3.org/2002/07/owl#sameAs")) {
							sameAs = linkPredicate;
							isSameAs = true;
						}
					}
					catch (ConstraintViolationException e) {
						LinkType lt = (LinkType) getDbObject(LinkType.class, o);
						linkPredicate = lt.getIdLinkType();
					}
				}
			}
			else if (p.equals(propObject.toString())) {
				++objOverall;
				try {
					linkObject = createRDFSResource(o);
				}
				catch (ConstraintViolationException e) {
					++objAlreadyExisting;
					
					// object is already in DB: get object from DB, return id
					List<RDFSResource> resList = getIdFromDb(RDFSResource.class, o, "uri");
					linkObject = resList.get(0).getIdResource();
				} // TODO finally block? check if linkObject is still 0?
			}

			if (linkSubject != 0 && linkPredicate != 0 && linkObject != 0) {
				try {
					createLink(s, linkSubject, linkPredicate, linkObject, hashMappingUrl);
				}
				catch (ConstraintViolationException e) {
					System.out.println("Link already existing: " + s);
				}
				
				linkSubject = 0;
				linkPredicate = 0;
				linkObject = 0;
			}

			if (p.equals(hasSource.toString()) || p.equals(hasTarget.toString())) {
				try {
					long idSource = createSource(o);
					createMappingHasSource(idSource, hashMappingUrl);
				}
				catch (ConstraintViolationException e) {
					System.out.println("Source or source/mapping relation already existing: " + o);
				}
			}
			
			// TODO User
			// TODO Framework
			if (p.equals(wasGeneratedBy.toString())) {
				try {
					createAlgorithm(o, hashMappingUrl);
					System.out.println("Algorithm added");
				}
				catch (ConstraintViolationException e) {
					
				}
			}
			
			// TODO not working in test, enable later, not EVERY generatedAtTime is valid for the mapping
			if (p.equals(generatedAtTime.toString()) ) { //&& s.equals(hashMappingUrl)) { 
				Mapping mapping = (Mapping) getDbObject(Mapping.class, s);	

				// discard ^^xsd:date at the end
				String time = SQLUtils.convertDateSeparators(o.substring(0, o.lastIndexOf("^") - 1));
				Timestamp value = Timestamp.valueOf(time);
				
				mapping.setTimeGenerated(value);
				
				try {
					getSessionAndSave(mapping);
				} catch (ConstraintViolationException e) {
					e.printStackTrace();
				}
				System.out.println("Date added");
			}
		}
		
		System.out.println(objAlreadyExisting + "objects already existing out of " + objOverall + " objects overall.");
	}

	private void createMappingHasSource(long idSource, String hashMappingUrl) throws ConstraintViolationException {
		MappingHasSource mhs = new MappingHasSource();
		mhs.setIdSource(idSource);
		mhs.setHashMapping(hashMappingUrl);
		
		getSessionAndSave(mhs);	
	}

	/**
	 * Get a specific DB object, only working if primary key is String
	 * @param myClass table to search in
	 * @param searchTerm name of object to fetch
	 * @return resulting object of class myClass
	 */
	private <T> Object getDbObject(Class<T> myClass, String searchTerm) {
		Session session = InitSessionFactory.getInstance().getCurrentSession();
		@SuppressWarnings("unused")
		Transaction tx = session.beginTransaction();
		
		return session.load(myClass, new String(searchTerm));
	}

	/**
	 * Creates an algorithm, url needs to be unique, is NOT good implemented for now!
	 * @param algoUri
	 * @param hashMappingUrl
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private void createAlgorithm(String algoUri, String hashMappingUrl) throws ConstraintViolationException {
		// TODO not really unique
		Algorithm algorithm = new Algorithm();
		algorithm.setUri(algoUri);
		algorithm.setHashMapping(hashMappingUrl);
		
		getSessionAndSave(algorithm);
	}

	private long createSource(String uri) throws ConstraintViolationException {
		Source source = new Source();
		source.setUri(uri);
		
		getSessionAndSave(source);
		
		return source.getIdSource();
	}
	
	/**
	 * Get db session and search for string in specific column (based on parameter targetCol) of table.
	 * 
	 * Likely to be not needed for all classes. Primary Key and other indexed structures can return error code if object is already created.
	 * @param <T> table to be searched in
	 * @param targetString search criterion
	 * @param targetCol search in this column
	 * @return list of instances applying to criterion
	 */
	private <T> List<T> getIdFromDb(Class<T> myClass, String targetString, String targetCol) {
		Session session = InitSessionFactory.getInstance().getCurrentSession();
		@SuppressWarnings("unused")
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(myClass);
		
		criteria = createRestrictions(myClass, targetString, targetCol, criteria);
		
		return criteria.list();
	}

	/**
	 * @param myClass
	 * @param targetString
	 * @param criteria
	 */
	private <T> Criteria createRestrictions(Class<T> myClass, String targetString, String targetCol,
			Criteria criteria) {
		// restrict to column, search for the string target
		if (targetCol.equals("")) {
			if (myClass.equals(RDFSResource.class) || myClass.equals(LinkType.class) 
					|| myClass.equals(Source.class) || myClass.equals(Algorithm.class)) {
				criteria.add(Restrictions.eq("uri", targetString));
			} 
			else if (myClass.equals(Mapping.class)) {
				criteria.add(Restrictions.eq("hashMapping", targetString));
			}
			else if (myClass.equals(Link.class)) {
				criteria.add(Restrictions.eq("hashLink", targetString));
			}
		}
		else {
			criteria.add(Restrictions.eq(targetCol, targetString));
		}
		return criteria;
	}

	/**
	 * Create a new RDFSResource with unique ID.
	 * @param uri
	 * @return Unique ID of the newly generated RDFSResource
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private long createRDFSResource(String uri) throws ConstraintViolationException {
		RDFSResource res = new RDFSResource();
		res.setUri(uri);
		
		getSessionAndSave(res);

		return res.getIdResource();
	}
	
	/**
	 * Establish a session to the database and save the (generic) hibernate object.
	 * @param hibObject
	 */
	private <T> void getSessionAndSave(T hibObject) throws ConstraintViolationException {
		
		Session session = InitSessionFactory.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try {
			session.save(hibObject);
			tx.commit();
		}
		catch (ConstraintViolationException e) {
	        tx.rollback();
	        throw e;
		}
	}
	
	/**
	 * Create a new LinkType with unique ID.
	 * @param uri
	 * @return Unique ID of the newly generated LinkType
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private long createLinkType(String uri) throws ConstraintViolationException {
		LinkType linkType = new LinkType();
		linkType.setUri(uri);
		
		getSessionAndSave(linkType);

		return linkType.getIdLinkType();
	}
	
	/**
	 * test
	 * @return
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private User createUser() throws ConstraintViolationException {
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
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private String createLink(String hashLink, long subject, long predicate, long object, String hashMappingUrl) throws ConstraintViolationException {

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
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private String createMapping(String hashUrl) throws ConstraintViolationException {
		//Jena getLocalName() not working correct with hash
		//String hash  = hashUrl.substring(hashUrl.lastIndexOf(LLProp.getString("fragmentIdentifier")) + 1);

		Mapping mapping = new Mapping();
		mapping.setHashMapping(hashUrl);
		
		getSessionAndSave(mapping);
		
		return mapping.getHashMapping();
	}
}
