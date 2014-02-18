package de.linkinglod.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.*;

import de.linkinglod.db.User;
import de.linkinglod.rdf.RDFMappingProcessor;

import java.security.*;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 * 
 * NOT much longer NEEDED, deprecated.
 * 
 * TODO look for objects which are a literal and not URI
 * TODO testing
 * TODO where do we need to differentiate between literals/resources for objects?
 */
public class DataGenerator {

	// only used in main, should be removed later.
	private static String fileLocation = LLProp.getString("fileLocation");
	private static Logger log = LoggerFactory.getLogger(DataGenerator.class);

    private static DBCommunication dbComm = null;
	private Model originalModel = null;
    private Model transformedModel = null;
	
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
		
//		System.out.println("tmpdir :" + System.getProperty("java.io.tmpdir"));
//    	Map<String, String> nsMap = model.getNsPrefixMap();
//    	System.out.println("nsMap.toString(): " + nsMap.toString());
//    	Collection<String> colNs = nsMap.values();
//    	for (String s: colNs) {
//    		System.out.println(s);
//    	}
				
		RDFMappingProcessor mappingProcessor = new RDFMappingProcessor(LLProp.getString("fileLocation"));
		
    	User demoUser = new User(); // TODO next: manage user login
    	demoUser.setIdUser(1);
    	demoUser.setName("Demo User");
    	
    	model = mappingProcessor.transform(model, demoUser, new Date());

    	//model.write(System.out, "N-TRIPLE");
    	
    	//dbComm = new DBCommunication();
    	
		//dbComm.write(LLProp.getString("TripleStore.graph"), model);
	}
	
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
	 * @throws IOException 
	 */
	public static Model generateModelFromStream(InputStream stream) throws IOException {
		Model model = ModelFactory.createDefaultModel();
				
		model.read(stream, null, LLProp.getString("tripleInputFormat"));
		log.debug("Read " + model.size() + " elements.");
        stream.close();  
        
		return model;
	}

}
