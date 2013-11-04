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

import de.linkinglod.service.TripleStoreCommunication;
import de.linkinglod.service.UploadFileService;

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

	private static Preferences prefs;
	private static String fileLocation = "/home/markus/Mapping/Links/geonames-dbpedia.nt";
	private Logger log = LoggerFactory.getLogger(DataGenerator.class);

	
	public DataGenerator(String fileLocation, Logger log) {
		setPreferences(fileLocation);
		this.log = log;

	}
	
	/**
	 * Temp main, should be removed later
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) {

		final Logger tempLog = LoggerFactory.getLogger(DataGenerator.class);

		DataGenerator dataGenerator = new DataGenerator(fileLocation, tempLog);
		InputStream stream = generateStreamFromFile(fileLocation);
		
	    Model model = null;
	    TripleStoreCommunication comm = null;
	    
	    if (stream != null) {
	    	model = dataGenerator.generateModelFromStream(stream);
			comm = new TripleStoreCommunication(model);
			dataGenerator.processData(model, comm, prefs);
	    }
		
		// TODO
		// read file
		//comm.loadTempData(model);
		// convert each statement
		// check if already existent, write to store

		//comm.executeQuery(model, "select * where {?s ?p ?o} limit 10");
	}

	private static InputStream generateStreamFromFile(String fileLocation) {
		InputStream is = null;
		try {
			is = new FileInputStream(fileLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return is;
	}

	public void setPreferences(String fileLocation) {
		prefs = Preferences.userNodeForPackage(this.getClass());
		//prefs.put("fileName", "/home/markus/Mapping/Links/geonames-dbpedia.nt");
		prefs.put("fileName", fileLocation);
		prefs.put("subjectAttr", "hasEntitySource");
		prefs.put("objectAttr", "hasEntityTarget");
		prefs.put("linkType", "hasLinkType");
		prefs.put("ns", "http://linkinglod.eu");
	}

	/**
	 * Reification process. Each statements gets converted and submitted as a single _model_. Better would be single statements.
	 * TODO How are large amounts of statements performing?
	 * @param model
	 * @param comm
	 * @param prefs
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public void processData(Model model, TripleStoreCommunication comm, Preferences prefs) {
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

		while (iter.hasNext()) {
			Statement statement = iter.nextStatement();  
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

			convertedModel = convertStatement(s, p, o, md5, prefs, convertedModel);
			//addStatementsToFile(convertedModel);
		}
		
		log.debug("literalCount: " + literalCount);
		log.debug("resourceCount: " + resourceCount);
		log.debug("others: " + others);
		//convertedModel.write(System.out, "N-TRIPLE");
	}

	private Model convertStatement(Resource s, Property p, RDFNode o,
			String md5, Preferences prefs, Model convertedModel) {
		
		// TODO if ns ends with /, dont add / again
		String ns = prefs.get("ns", "");

		Resource resource = ResourceFactory.createResource(ns + "/" + md5);
		Property propS = ResourceFactory.createProperty(ns + "/" + prefs.get("subjectAttr", ""));
		Property propP = ResourceFactory.createProperty(ns + "/" + prefs.get("linkType", ""));
		Property propO = ResourceFactory.createProperty(ns + "/" + prefs.get("objectAttr", ""));
		
		convertedModel.add(resource, propS, s)
				.add(resource, propP, p)
				.add(resource, propO, o);
		
		return convertedModel;
	}

	private static void addStatementsToFile(Model model) {
		// check check
		// convertedModel.write(System.out, "N-TRIPLE");
		// TODO check if file is empty at start
		String outFileString = prefs.get("fileName", "") + ".out";
		File outFile = new File(outFileString);

		boolean doAppend = false;
		if (outFile.exists()) {
			doAppend = true;
		}

		try {
			FileWriter outFileWriter = new FileWriter(outFileString, doAppend);
			BufferedWriter outBuffer = new BufferedWriter(outFileWriter);

			model.write(outBuffer, "TURTLE");
			outBuffer.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static String buildMd5String(Resource s, Property p, RDFNode o) {
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

	private static String computeChecksum(MessageDigest md, String spo) {
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

		if (stream != null) {
			model.read(stream, null, "N-TRIPLE");
		} else {
			System.err.println("Cannot read " + stream + ". Correct N-TRIPLE format?");;
		}

		return model;
	}

	public Preferences getPrefs() {
		return prefs;
	}

	public void setPrefs(Preferences prefs) {
		DataGenerator.prefs = prefs;
	}

}
