package de.linkinglod.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.hp.hpl.jena.rdf.model.*;

import de.linkinglod.service.DBCommunication;
import de.linkinglod.service.LLProp;
import de.linkinglod.service.TripleStoreCommunication;

import java.security.*;
import java.util.List;


import org.apache.commons.codec.binary.Hex;
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

    private DBCommunication dbComm = null;
    private TripleStoreCommunication tsComm = null;
	private Model originalModel = null;
    private Model transformedModel = null;
    private static String hashMapping = null;
	
	/**
	 * TODO Do not work in the constructor!
	 * @param stream
	 * @param fileLocation
	 * @param log
	 * @throws NoSuchAlgorithmException
	 */
	public DataGenerator(InputStream stream, String fileLocation, Logger log) throws NoSuchAlgorithmException {
		
		this.log = log; 
		tsComm = new TripleStoreCommunication(transformedModel);
		dbComm = new DBCommunication();

	    if (stream != null) {
	    	originalModel = generateModelFromStream(stream);
	    	System.out.println("generateModelFromStream().isEmpty(): " + originalModel.isEmpty());

			transformedModel = processData(originalModel);
			System.out.println("processData(model): done");

			tsComm.saveModel(transformedModel);
			dbComm.saveModel(transformedModel);
			System.out.println("saveModel(): done");
	    } else {
			System.err.println("Cannot read InputStream " + stream + ". Correct N-TRIPLE format?");
		}
	}
	
	/**
	 * Temp main, will be removed later
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		InputStream stream = generateStreamFromFile(fileLocation);
		hashMapping = computeChecksum();
		
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

	private static InputStream generateStreamFromFile(String fileLocation) throws FileNotFoundException {
		
		InputStream stream = null;
		stream = new FileInputStream(fileLocation);
		
		if (stream != null) {
			log.debug("File " + fileLocation + " read.");
		}
		return stream;
	}

	/**
	 * Reification process. Each statements gets converted and added to a new model.
	 * TODO How are large amounts of statements performing?
	 * @param model
	 * @throws NoSuchAlgorithmException
	 */
	public Model processData(Model model) throws NoSuchAlgorithmException {
		
		StmtIterator modelIterator = model.listStatements();
		List<Statement> listModel = modelIterator.toList();
		Model convertedModel = ModelFactory.createDefaultModel();
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();;

		for (Statement statement: listModel) {
			Resource s = statement.getSubject();     
			Property p = statement.getPredicate(); 
			RDFNode o = statement.getObject();

			String md5 = computeChecksum(md, buildMd5String(s, p, o));

			convertedModel = convertStatement(s, p, o, md5, convertedModel);
		}
		
		//convertedModel.write(System.out, "N-TRIPLE");
		return convertedModel;
	}

	/**
	 * Convert a single triple line to the format which is needed in the TripleStore. Each triple gets splitted in at least 4 triples.
	 * @param s subject of original triple
	 * @param p predicate of original triple
	 * @param o object of original triple
	 * @param md5 MD5 hash of concatenated original triple
	 * @param convertedModel this model is expanded by 4 triples with each line of the original triples
	 * @return completed and fully converted model
	 */
	private Model convertStatement(Resource s, Property p, RDFNode o, String md5, Model convertedModel) {
		
		// TODO put / into preferences file
		String ns = LLProp.getString("ns");
		String lim = LLProp.getString("delimiter");
		String vocProp = LLProp.getString("vocabularyProperty");
		String propString = ns + lim + vocProp + lim;
		
		Resource resource = ResourceFactory.createResource(ns + lim + 
														LLProp.getString("vocabularyLink") + 
														LLProp.getString("fragmentIdentifier") + md5);
		
		Property propS = ResourceFactory.createProperty(propString + LLProp.getString("subjectAttribute"));
		Property propP = ResourceFactory.createProperty(propString + LLProp.getString("linkType"));
		Property propO = ResourceFactory.createProperty(propString + LLProp.getString("objectAttribute"));
		Property propM = ResourceFactory.createProperty(propString + LLProp.getString("hashMapping"));

		convertedModel.add(resource, propS, s)
				.add(resource, propP, p)
				.add(resource, propO, o)
				.add(resource, propM, hashMapping);
		
		// TODO create mapping information
		
		return convertedModel;
	}

	private void addStatementsToFile(Model model) throws IOException {
		// convertedModel.write(System.out, "N-TRIPLE");
		String outFileString = fileLocation + ".out";
		File outFile = new File(outFileString);

		boolean doAppend = false;
		if (outFile.exists()) {
			doAppend = true;
		}
		
		FileWriter outFileWriter = new FileWriter(outFileString, doAppend);
		BufferedWriter outBuffer = new BufferedWriter(outFileWriter);

		model.write(outBuffer, LLProp.getString("tripleOutputFormat")); //$NON-NLS-1$
		outBuffer.close();
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
		// TODO Check if checksum is correct calculated!
		if (o instanceof Resource) {
			statement += o.toString();
		} else {
			statement+= "\"" + o.toString() + "\"";
		}
		statement += " .";

		return statement;
	}

	/**
	 * Checksum is calculated, string is converted to UTF8 previously.
	 * @param md
	 * @param text
	 * @return checksum as hexadecimal value
	 */
	private String computeChecksum(MessageDigest md, String text) {
		
		byte[] stringToUTF8byte = text.getBytes(Charset.forName("UTF8"));
		md.update(stringToUTF8byte);

		final byte[] resultDigest = md.digest();
		final String result = new String(Hex.encodeHex(resultDigest));
		return result;
	}
	
	
	/**
	 * Generates MD5 hash of the previously chosen file (mapping).
	 * @param md
	 * @return
	 * @throws IOException
	 * TODO Check, if this checksum is correct for the mapping, do we need to consider UTF8 stuff?
	 * @throws NoSuchAlgorithmException 
	 */
	private static String computeChecksum() throws IOException, NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		try (InputStream is = Files.newInputStream(Paths.get(fileLocation))) {
			  DigestInputStream dis = new DigestInputStream(is, md);
			}
		byte[] digest = md.digest();
		
		return new String(Hex.encodeHex(digest));
	}

	/**
	 * Build a Jena model from (N-TRIPLE conform) file
	 * @param stream
	 * @return
	 */
	public Model generateModelFromStream(InputStream stream) {
		
		Model model = ModelFactory.createDefaultModel();
		model.read(stream, null, LLProp.getString("tripleInputFormat")); //$NON-NLS-1$
		System.out.println("Read " + model.size() + " elements.");

		return model;
	}
}
