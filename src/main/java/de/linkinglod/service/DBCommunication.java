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
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import de.linkinglod.db.Algorithm;
import de.linkinglod.db.Framework;
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
	private User fakeUser = null;

	// load namespaces
	private String rdf = ontoModel.getNsPrefixURI("rdf");
	private String prov = ontoModel.getNsPrefixURI("prov");
	private String llont = ontoModel.getNsPrefixURI("llont");
	private String llalg = ontoModel.getNsPrefixURI("llalg");
	private String lldat = ontoModel.getNsPrefixURI("lldat");

	// load properties
	private Property propSubject = ontoModel.getProperty(rdf + "subject");
	// check occurrence if more than sameAs links are supported/needed
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
	 * Temp main. Not needed!?
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
	 * Complete processing of the given Jena model, each statement is parsed and, if needed, mapped to the database via Hibernate.
	 * @param dbModel
	 * @param graph
	 */
	@Override
	public void write(String graph, Model jenaModel) {
		
		log.debug("DBCommunication.write()");

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
			String subject = statement.getSubject().toString();
			String predicate = statement.getPredicate().toString();
			String object = statement.getObject().toString();
			
			// isMappingCreated ensures no unneeded DB query at this point
			if (!isMappingCreated) {
				if (predicate.equals(wasDerivedFrom.toString())) {					
					try {
						hashMappingUrl = createMapping(object);
						isMappingCreated = true;
					}
					catch (ConstraintViolationException e) {
					}
				}
			}

			// which RDFSResource is S, P, O in the new Link object
			if (predicate.equals(propSubject.toString())) {
				++objOverall;
				try {
					linkSubject = createRDFSResource(object);
				}
				catch (ConstraintViolationException e) {
					++objAlreadyExisting;
					
					RDFSResource res = (RDFSResource) getDbObjectFromAttr(RDFSResource.class, object, "uri");
					linkSubject = res.getIdResource();

				} // TODO finally block? check if linkObject is still 0?
			}
			else if (predicate.equals(propPredicate.toString())) {

				// TODO this is only an assumption: if one statement is sameAs, others are MAYBE not sameAs
				// but at the moment only sameAs is supported!
				if (isSameAs) {
					linkPredicate = sameAs;
				}
				else {
					try {
						linkPredicate = createLinkType(object);
						if (predicate.equals("http://www.w3.org/2002/07/owl#sameAs")) {
							sameAs = linkPredicate;
							isSameAs = true;
						}
					}
					catch (ConstraintViolationException e) {
						LinkType lt = (LinkType) getDbObjectFromAttr(LinkType.class, object, "uri");
						linkPredicate = lt.getIdLinkType();
					}
				}
			}
			else if (predicate.equals(propObject.toString())) {
				++objOverall;
				try {
					linkObject = createRDFSResource(object);
				}
				catch (ConstraintViolationException e) {
					++objAlreadyExisting;
					
					RDFSResource res = (RDFSResource) getDbObjectFromAttr(RDFSResource.class, object, "uri");
					linkObject = res.getIdResource();
				} // TODO finally block? check if linkObject is still 0?
			}

			if (linkSubject != 0 && linkPredicate != 0 && linkObject != 0) {
				try {
					createLink(subject, linkSubject, linkPredicate, linkObject, hashMappingUrl);
				}
				catch (ConstraintViolationException e) {
					log.debug("Link already existing: " + subject);
				}
				
				linkSubject = 0;
				linkPredicate = 0;
				linkObject = 0;
			}

			if (predicate.equals(hasSource.toString()) || predicate.equals(hasTarget.toString())) {
				try {
					long idSource = createSource(object);
					createMappingHasSource(idSource, hashMappingUrl);
				}
				catch (ConstraintViolationException e) {
					log.debug("Source or source/mapping relation already existing: " + object);
				}
			}
			
			// TODO User
			// TODO Framework
			if (predicate.equals(wasGeneratedBy.toString())) {
				try {
					createAlgorithm(object, hashMappingUrl);
					log.debug("Algorithm added");
				}
				catch (ConstraintViolationException e) {
					
				}
			}
			
			// TODO not working in test, enable later, not EVERY generatedAtTime is valid for the mapping
			if (predicate.equals(generatedAtTime.toString())) { 

				String timeAndTimezone = SQLUtils.convertDateSeparators(object);
				String time = SQLUtils.toTimestamp(timeAndTimezone);
				Timestamp value = Timestamp.valueOf(time);
				
				if (subject.equals(hashMappingUrl)) {
					Mapping mapping = (Mapping) getDbObjectFromId(Mapping.class, subject);
					mapping.setTimeGenerated(value);

					try {
						getSessionAndSave(mapping);
					} catch (ConstraintViolationException e) {
						e.printStackTrace();
					}
					log.debug("Mapping Date added");
				}
				if (subject.contains("http://www.linklion.org/algorithm/")) { // TODO rework
					Algorithm algo = (Algorithm) getDbObjectFromAttr(Algorithm.class, subject, "url");
					algo.setCreationDate(value);

					try {
						getSessionAndSave(algo);
					} catch (ConstraintViolationException e) {
						e.printStackTrace();
					}
					log.debug("Algorithm Date added");
				}
			}
			
			// Framework
			if (predicate.equals(wasGeneratedBy.toString())) {
				try {
					createFramework(object);
				}
				catch (ConstraintViolationException e) {
					log.debug("Framework already existing: " + object);
				}
			}
		}
		
		InitSessionFactory.getInstance().getCurrentSession().close();
		System.out.println(objAlreadyExisting + "objects already existing out of " + objOverall + " objects overall.");
	}

	/**
	 * Create a new Framework in database (via Hibernate).
	 * @param name
	 * @return
	 */
	private long createFramework(String name) {
		Framework f = new Framework();
		f.setName(name);
		f.setIdOwner(fakeUser.getIdUser());
		
		getSessionAndSave(f);			
		return f.getIdFramework();
	}

	/**
	 * Create a new MappingHasSource in database (via Hibernate).
	 * @param idSource
	 * @param hashMappingUrl
	 * @throws ConstraintViolationException
	 */
	private void createMappingHasSource(long idSource, String hashMappingUrl) throws ConstraintViolationException {
		MappingHasSource mhs = new MappingHasSource();
		mhs.setIdSource(idSource);
		mhs.setHashMapping(hashMappingUrl);
		
		getSessionAndSave(mhs);	
	}

	/**
	 * Creates an algorithm in the database (via Hibernate), url needs to be unique, is NOT good implemented for now!
	 * @param algoUrl
	 * @param hashMappingUrl
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private void createAlgorithm(String algoUrl, String hashMappingUrl) throws ConstraintViolationException {
		// TODO not really unique
		Algorithm algorithm = new Algorithm();
		algorithm.setUrl(algoUrl);
		algorithm.setHashMapping(hashMappingUrl);
		
		getSessionAndSave(algorithm);
	}

	/**
	 * @param uri
	 * @return
	 * @throws ConstraintViolationException
	 */
	private long createSource(String uri) throws ConstraintViolationException {
		Source source = new Source();
		source.setUri(uri);
		
		getSessionAndSave(source);
		
		return source.getIdSource();
	}

	/**
	 * Get a specific DB object, only working if primary key is String
	 * @param myClass table to search in
	 * @param searchTerm name of object to fetch
	 * @return resulting object of class myClass
	 */
	private <T> Object getDbObjectFromId(Class<T> myClass, String searchTerm) {
		Session session = InitSessionFactory.getInstance().getCurrentSession();
		@SuppressWarnings("unused")
		Transaction tx = session.beginTransaction();
		// LinkType sameAs
		Object dbObject = session.load(myClass, new String(searchTerm));
		
		return dbObject;
	}
	
	/**
	 * Get db session and search for string in specific column (based on parameter targetCol) of table.
	 * 
	 * TODO Check if this is fixed now: Likely to be not needed for all classes. Primary Key and other 
	 * indexed structures can return error code if object is already created.
	 * 
	 * @param <T> table to be searched in
	 * @param targetString search criterion
	 * @param targetCol search in this column
	 * @return list of instances applying to criterion
	 */
	private <T> Object getDbObjectFromAttr(Class<T> myClass, String targetString, String targetCol) {
		Session session = InitSessionFactory.getInstance().getCurrentSession();
		@SuppressWarnings("unused")
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(myClass);
		
		criteria = createRestrictions(myClass, targetString, targetCol, criteria);
		
		return criteria.list().get(0);
	}

	/**
	 * Search for a specific column in the database table of the given Java class.
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
			else if (myClass.equals(LinkType.class)) {
				criteria.add(Restrictions.eq("uri", targetString));
			}
		}
		else {
			criteria.add(Restrictions.eq(targetCol, targetString));
		}
		return criteria;
	}

	/**
	 * Create a new RDFSResource (via Hibernate) with unique ID.
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
	 * TODO where to end session?
	 */
	public <T> void getSessionAndSave(T hibObject) throws ConstraintViolationException {
		
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
	 * Create a new LinkType (via Hibernate) with unique ID.
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
	 *  Create a User object in the database (via Hibernate).
	 * @return
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private User createUser() throws ConstraintViolationException {
		User testUser = new User();
		
		testUser.setName("foo bar");
		getSessionAndSave(testUser);

		return testUser;
	}

	/**
	 * Create a Link object in the database (via Hibernate) and return the hashLink of the already saved link.
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
		link.setRes1Id(subject);
		link.setRes2Id(object);
		link.setLinkType(predicate);
		link.setHashMapping(hashMappingUrl);
		
		getSessionAndSave(link);
		
		return link.getHashLink();
	}

	/**
	 * Create a Mapping object in the database (via Hibernate).
	 * @param hashUrl
	 * @return 
	 * @throws MySQLIntegrityConstraintViolationException 
	 */
	private String createMapping(String hashUrl) throws ConstraintViolationException {
		//Jena getLocalName() not working correct with hash
		//String hash  = hashUrl.substring(hashUrl.lastIndexOf(LLProp.getString("fragmentIdentifier")) + 1);

		Mapping mapping = new Mapping();
		mapping.setHashMapping(hashUrl);
		mapping.setIdOwner(fakeUser.getIdUser());
		mapping.setIdUploader(fakeUser.getIdUser());
		
		getSessionAndSave(mapping);
		
		return mapping.getHashMapping();
	}

	public void createUser(User demoUser) {
		fakeUser = demoUser;
	}
}
