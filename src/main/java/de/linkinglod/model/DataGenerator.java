package de.linkinglod.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.hp.hpl.jena.rdf.model.*;

import de.linkinglod.service.DBCommunication;
import de.linkinglod.service.LinkingLodProperties;
import de.linkinglod.service.TripleStoreCommunication;

import java.security.*;
import java.util.prefs.Preferences;


import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author markus
 * TODO look for objects which are a literal and not URI
 */
public class DataGenerator {

	// only used in main, should be removed later. TODO its hard coded, change it!
	private static String fileLocation = LinkingLodProperties.getString("DataGenerator.fileLocation"); //$NON-NLS-1$
	private static Logger log = LoggerFactory.getLogger(DataGenerator.class);

	private Model originalModel = null;
    private Model transformedModel = null;
    private DBCommunication dbComm = null;
    private TripleStoreCommunication tsComm = null;
	
	public DataGenerator(InputStream stream, String fileLocation, Logger log) {
		
		this.log = log; 
		tsComm = new TripleStoreCommunication(transformedModel);
		dbComm = new DBCommunication();

	    if (stream != null) {
	    	originalModel = generateModelFromStream(stream);
	    	System.out.println("generateModelFromStream().isEmpty(): " + originalModel.isEmpty());

			transformedModel = processData(originalModel);
			System.out.println("processData(model): done");

			tsComm.saveModel(transformedModel);
			dbComm.saveModel(originalModel);
			System.out.println("saveModel(): ");
	    } else {
			System.err.println("Cannot read InputStream " + stream + ". Correct N-TRIPLE format?");
		}
	}
	
	/**
	 * Temp main, will be removed later
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) {

		InputStream stream = generateStreamFromFile(fileLocation);
		DataGenerator dataGenerator = new DataGenerator(stream, fileLocation, log);	
		
//	    if (stream != null) {
//	    	model = generateModelFromStream(stream);
//	    	System.out.println("generateModelFromStream(): done!");
//			processData(model);
//			System.out.println("processData(model): done!");
//			dbCommTest.saveModel(model);
//			System.out.println("saveModel(): ");
//		} else {
//			System.err.println("Cannot read InputStream " + stream + ". Correct N-TRIPLE format?");
//		}
		//comm.executeQuery(model, "select * where {?s ?p ?o} limit 10");
	}

	private static InputStream generateStreamFromFile(String fileLocation) {
		InputStream stream = null;
		
		try {
			stream = new FileInputStream(fileLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (stream != null) {
			log.debug("File " + fileLocation + " read sucessful.");
		}
		return stream;
	}

	/**
	 * Reification process. Each statements gets converted and submitted as a single _model_. Better would be single statements.
	 * TODO How are large amounts of statements performing?
	 * @param model
	 * @param prefs
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public Model processData(Model model) {
		
		StmtIterator iter = model.listStatements();
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.reset();
		
		Model convertedModel = ModelFactory.createDefaultModel();
		int literalCount = 0;
		int resourceCount = 0;
		int others = 0;

		for (Statement statement: iter.toList()) {
			Resource s = statement.getSubject();     
			Property p = statement.getPredicate(); 
			RDFNode o = statement.getObject();
			
			// TODO do we need to differentiate between literals/resources for objects?
			// if it's a literal, it can be no link to another real world entity
			if (o.isLiteral()) {
				//System.out.println("isLiteral: " + o.toString());
				++literalCount;
			} else if (o.isResource()) {
				//System.out.println("isResource: " + o.toString());
				++resourceCount;
			} else {
				++others;
			}

			String md5 = computeChecksum(md, buildMd5String(s, p, o));

			convertedModel = convertStatement(s, p, o, md5, convertedModel);
			//addStatementsToFile(convertedModel);
		}
		
		log.debug("literalCount: " + literalCount);
		System.out.println("resourceCount: " + resourceCount);
		log.debug("others: " + others);
		//convertedModel.write(System.out, "N-TRIPLE");
		return convertedModel;
	}

	private Model convertStatement(Resource s, Property p, RDFNode o,
			String md5, Model convertedModel) {
		
		// TODO if ns ends with /, dont add / again
		String ns = LinkingLodProperties.getString("DataGenerator.ns");

		Resource resource = ResourceFactory.createResource(ns + "/" + md5);
		Property propS = ResourceFactory.createProperty(ns + "/" + LinkingLodProperties.getString("DataGenerator.subjectAttribute"));
		Property propP = ResourceFactory.createProperty(ns + "/" + LinkingLodProperties.getString("DataGenerator.linkType"));
		Property propO = ResourceFactory.createProperty(ns + "/" + LinkingLodProperties.getString("DataGenerator.objectAttribute"));
		
		convertedModel.add(resource, propS, s)
				.add(resource, propP, p)
				.add(resource, propO, o);
		
		return convertedModel;
	}

	private void addStatementsToFile(Model model) {
		// check check
		// convertedModel.write(System.out, "N-TRIPLE");
		// TODO check if file is empty at start
		String outFileString = fileLocation + ".out";
		File outFile = new File(outFileString);

		boolean doAppend = false;
		if (outFile.exists()) {
			doAppend = true;
		}

		try {
			FileWriter outFileWriter = new FileWriter(outFileString, doAppend);
			BufferedWriter outBuffer = new BufferedWriter(outFileWriter);

			model.write(outBuffer, LinkingLodProperties.getString("DataGenerator.tripleOutputFormat")); //$NON-NLS-1$
			outBuffer.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Build String which is used for generating the MD5 hash, if object is not a resource, surround it with ".
	 * @param s subject
	 * @param p predicate
	 * @param o object
	 * @return 
	 */
	private String buildMd5String(Resource s, Property p, RDFNode o) {
		String statement = s.toString() + p.toString();
		// TODO should literal be modified with quotes?
		if (o instanceof Resource) {
			statement += o.toString();
		} else {
			statement+= "\"" + o.toString() + "\"";
		}
		statement += " .";

		return statement;
	}

	private String computeChecksum(MessageDigest md, String spo) {
		byte[] stringToUTF8byte = spo.getBytes(Charset.forName("UTF8"));
		md.update(stringToUTF8byte);

		final byte[] resultDigest = md.digest();
		final String result = new String(Hex.encodeHex(resultDigest));
		return result;
	}
	

	/**
	 * Build a Jena model from (N-TRIPLE conform) file
	 * @param stream
	 * @return
	 */
	public Model generateModelFromStream(InputStream stream) {
		Model model = ModelFactory.createDefaultModel();
		model.read(stream, null, LinkingLodProperties.getString("DataGenerator.tripleInputFormat")); //$NON-NLS-1$
		System.out.println("Read " + model.size() + " elements.");

		return model;
	}
}
