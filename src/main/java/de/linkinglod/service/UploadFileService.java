package de.linkinglod.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.PropertyConfigurator;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;

import de.linkinglod.db.User;
import de.linkinglod.io.Reader;
import de.linkinglod.rdf.RDFMappingProcessor;
import de.linkinglod.rdf.TripleStoreWriter;
 
/**
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
@Path("/file")
public class UploadFileService implements Reader {
	
	private String fileLocation = "";
	private Model modelOut;
	private static Logger log = LoggerFactory.getLogger(UploadFileService.class);

	/**
	 * Upload MULTIPART_FORM_DATA file, save to Java temp directory.
	 * @param stream
	 * @param fileDetail
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream stream,
							   @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException, URISyntaxException {
		log.debug("File upload service triggered!");

		fileLocation = System.getProperty("java.io.tmpdir") + fileDetail.getFileName();
		writeToFile(stream, fileLocation);
		 
		Model model = read(fileLocation);
    	System.out.println("generateModelFromStream().isEmpty(): " + model.isEmpty());
    	
    	RDFMappingProcessor processor = new RDFMappingProcessor(fileLocation);
    	TripleStoreWriter tsw = new TripleStoreWriter();
    	DBCommunication dbComm = new DBCommunication();
    	
    	/*
    	 * TODO  note: Hibernate User() should not be used here, it is only for creating (hibernate) database User() objects! 
    	 * TODO REWORK this if users are implemented!
    	 */
    	User demoUser = new User(); // TODO next: manage user login
    	// idUser is created with auto_increment
    	demoUser.setName("Demo User");
		try {
	    	dbComm.getSessionAndSave(demoUser);
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
		}
    	
    	modelOut = processor.transform(model, demoUser, new Date());
    	
    	tsw.write(LLProp.getString("TripleStore.graph"), modelOut);
    	tsw.write(LLProp.getString("TripleStore.graph"), OntologyLoader.getOntModel());
    	
    	dbComm.createUser(demoUser);
    	
    	dbComm.write("TripleStore.graph", modelOut);
    	dbComm.write("TripleStore.graph", OntologyLoader.getOntModel());
    	
		String fileOutLocation = System.getProperty("java.io.tmpdir") + fileDetail.getFileName() + "_out";
		writeOutput(fileOutLocation);
		
		// FIXME Adding this throws an exception, but on line 162. Investigate why.
//		File f = new File(fileOutLocation);
//		String fileName = new SimpleDateFormat("yyyy-MM-dd_hhmmss").format(new Date()) + ".ttl";
//		f.renameTo(new File(System.getProperty("catalina.base") + "/webapps/LinkingLOD-0.0.1-SNAPSHOT/" + fileName));
//		String output = "<a href='" + fileName + "'>Download turtle file</a>";
			
		// TODO change this hack with something else
		return Response.status(200).entity("<html><head><meta http-equiv='refresh' content='0;url=../../success.html'></head></html>").build();
	}
	
	/**
	 * Read file and create Jena model.
	 * @param stream
	 * @param fileDetail
	 * @return Jena model
	 * @throws NoSuchAlgorithmException 
	 * @throws FileNotFoundException 
	 */
	@POST
	@Path("/read")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response readFile(@FormDataParam("file") InputStream stream,
							 @FormDataParam("file") FormDataContentDisposition fileDetail) 
									   throws NoSuchAlgorithmException, FileNotFoundException {
				
		log.debug("File read service triggered!");
		 
		return Response.status(200).entity("File read service triggered").build();
	}
 
	/**
	 * Save uploaded file to specified location.
	 * TODO should we save the file? It's redundant, but easy to download afterwards.
	 * @param stream
	 * @param fileLocation
	 * @throws IOException 
	 */
	private void writeToFile(InputStream stream, String fileLocation) throws IOException {

		OutputStream out = new FileOutputStream(new File(fileLocation));
		int read = 0;
		byte[] bytes = new byte[1024];

		out = new FileOutputStream(new File(fileLocation));
		while ((read = stream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}

	private void writeOutput(String fileLocation) throws IOException {

		OutputStream out = new FileOutputStream(new File(fileLocation));
		modelOut.write(out, LLProp.getString("tripleOutputFormat"));
		out.close();
	}

	@Override
	public Model read(String pathToFile) throws FileNotFoundException {
		
		InputStream stream = generateStreamFromFile(pathToFile);
		
		return generateModelFromStream(stream);
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
	public Model generateModelFromStream(InputStream stream) {
		
		Model model = ModelFactory.createDefaultModel();
		
		// TODO support different InputFormats like "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3".
		// Malformed statements have to be corrected/deleted manually, 
		// create error page for this:
		// org.apache.jena.riot.RiotException: [line: 1, col: 94] Unknown char: /(47;0x002F)
		// (example statement for this sort of error (missing <> in object): 
		// <http://create.canterbury.ac.uk/id/subject/DJK> 
		//   <http://www.w3.org/2002/07/owl#sameAs> 
		//   https://eprints.soas.ac.uk/id/subject/DJK .
		// )
		model.read(stream, null, LLProp.getString("tripleInputFormat"));
		log.debug("Read " + model.size() + " elements.");

		return model;
	}

	public Model getModelOut() {
		return modelOut;
	}

}