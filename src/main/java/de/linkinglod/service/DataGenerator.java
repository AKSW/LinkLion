package de.linkinglod.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.*;

import de.linkinglod.rdf.TripleStoreCommunication;
import de.linkinglod.util.MD5Utils;

import java.security.*;
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

    private DBCommunication dbComm = null;
    private TripleStoreCommunication tsComm = null;
	private Model originalModel = null;
    private Model transformedModel = null;
    private static String hashMapping = null;
	
	public DataGenerator(Model model) throws NoSuchAlgorithmException, IOException {
		originalModel = model;
		hashMapping = MD5Utils.computeChecksum();
		transformedModel = processData(originalModel);
		
		tsComm = new TripleStoreCommunication(transformedModel);
		dbComm = new DBCommunication();
		
		tsComm.saveModel(transformedModel);
		dbComm.saveModel(transformedModel);
	}

	/**
	 * Temp main, will be removed later
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		InputStream stream = generateStreamFromFile(fileLocation);
		System.out.println("Stream generated: ");
		Model model = generateModelFromStream(stream);
		System.out.println("Model generated with " + model.size() + " elements.");
		DataGenerator dataGenerator = new DataGenerator(model);	

		//comm.executeQuery(model, "select * where {?s ?p ?o} limit 10");
	}

	/**
	 * Reification process. Each statements gets converted and added to a new model.
	 * TODO How are large amounts of statements performing?
	 * @param model
	 * @throws NoSuchAlgorithmException
	 */
	public static Model processData(Model model) throws NoSuchAlgorithmException {
		
		StmtIterator modelIterator = model.listStatements();
		List<Statement> listModel = modelIterator.toList();
		Model convertedModel = ModelFactory.createDefaultModel();
		MD5Utils.reset();

		for (Statement statement: listModel) {
			Resource s = statement.getSubject();     
			Property p = statement.getPredicate(); 
			RDFNode o = statement.getObject();

			String md5 = MD5Utils.computeChecksum(s, p, o);

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
	private static Model convertStatement(Resource s, Property p, RDFNode o, String md5, Model convertedModel) {
		
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

		hashMapping = propString 
				+ LLProp.getString("vocabularyMapping") 
				+ LLProp.getString("fragmentIdentifier")
				+ hashMapping;
		
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
