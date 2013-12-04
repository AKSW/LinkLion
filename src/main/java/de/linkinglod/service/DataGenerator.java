package de.linkinglod.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.*;

import de.linkinglod.db.User;
import de.linkinglod.rdf.RDFMappingProcessor;
import de.linkinglod.rdf.TripleStoreCommunication;
import de.linkinglod.rdf.TripleStoreWriter;
import de.linkinglod.util.MD5Utils;

import java.security.*;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author markus
 * TODO look for objects which are a literal and not URI
 * TODO testing
 * TODO where do we need to differentiate between literals/resources for objects?
 */
public class DataGenerator {

	// only used in main, should be removed later. TODO its hard coded, change it!
	private static String fileLocation = LLProp.getString("fileLocation"); //$NON-NLS-1$
	private static Logger log = LoggerFactory.getLogger(DataGenerator.class);

    private static DBCommunication dbComm = null;
	private Model originalModel = null;
    private Model transformedModel = null;
    
	private static String ns = LLProp.getString("ns");
	private static String lim = LLProp.getString("delimiter");
	private static String vocProp = LLProp.getString("vocabularyProperty");
	private static String propString = ns + lim + vocProp + lim;
	
	public DataGenerator(Model model) throws NoSuchAlgorithmException, IOException {
//		originalModel = model;
//		transformedModel = processData(originalModel);
//				
//		dbComm = new DBCommunication();
//		
//		dbComm.write("foo", transformedModel);
	}

	/**
	 * Temp main, will be removed later
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		InputStream stream = generateStreamFromFile(LLProp.getString("fileLocation"));
		System.out.println("Stream generated: ");
		
		Model model = generateModelFromStream(stream);
		System.out.println("Model generated with " + model.size() + " elements.");
		
		RDFMappingProcessor mappingProcessor = new RDFMappingProcessor(LLProp.getString("fileLocation"));
    	User demoUser = new User(1, "Demo User"); // TODO next: manage user login
    	model = mappingProcessor.transform(model, demoUser, new Date());
    	//model.write(System.out, "N-TRIPLE");
    	
    	dbComm = new DBCommunication();

		dbComm.write(LLProp.getString("TripleStore.graph"), model);
    	
	}

	/**
	 * Reification process. Each statements gets converted and added to a new model.
	 * TODO How are large amounts of statements performing?
	 * @param model
	 * @throws NoSuchAlgorithmException
	 * @throws IOException 
	 */
//	public static Model processData(Model model) throws NoSuchAlgorithmException, IOException {
//		
//		StmtIterator modelIterator = model.listStatements();
//		List<Statement> listModel = modelIterator.toList();
//		Model convertedModel = ModelFactory.createDefaultModel();
//		MD5Utils.reset();
//		String fileHash = MD5Utils.computeChecksum(fileLocation);
//		String hashMapping = propString + LLProp.getString("vocabularyMapping") + LLProp.getString("fragmentIdentifier") + fileHash;
//		
//		Property propS = ResourceFactory.createProperty(propString + LLProp.getString("subjectAttribute"));
//		Property propP = ResourceFactory.createProperty(propString + LLProp.getString("linkType"));
//		Property propO = ResourceFactory.createProperty(propString + LLProp.getString("objectAttribute"));
//		Property propM = ResourceFactory.createProperty(propString + LLProp.getString("hashMapping"));
//
//		for (Statement statement: listModel) {
//			Resource s = statement.getSubject();     
//			Property p = statement.getPredicate(); 
//			RDFNode o = statement.getObject();
//			String md5 = MD5Utils.computeChecksum(s, p, o);
//			Resource resource = ResourceFactory.createResource(ns + lim + 
//					LLProp.getString("vocabularyLink") + 
//					LLProp.getString("fragmentIdentifier") + md5);
//
//			convertedModel.add(resource, propS, s)
//				.add(resource, propP, p)
//				.add(resource, propO, o)
//				.add(resource, propM, hashMapping);
//		}
//		
//		//convertedModel.write(System.out, "N-TRIPLE");
//		return convertedModel;
//	}
	
	/**
	 * Generate an InputStream from the file.
	 * @param fileLocation
	 * @return
	 * @throws FileNotFoundException
	 */
	private static InputStream generateStreamFromFile(String fileLocation) throws FileNotFoundException {
		InputStream stream = null;
		stream = new FileInputStream(fileLocation);
		log.debug("File " + fileLocation + " read.");
		
		return stream;
	}
	
	/**
	 * Build a Jena model from stream.
	 * @param stream
	 * @return
	 */
	public static Model generateModelFromStream(InputStream stream) {
		
		Model model = ModelFactory.createDefaultModel();
		model.read(stream, null, LLProp.getString("tripleInputFormat"));
		log.debug("Read " + model.size() + " elements.");

		return model;
	}

}
