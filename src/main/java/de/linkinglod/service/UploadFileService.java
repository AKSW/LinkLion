package de.linkinglod.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import de.linkinglod.io.Reader;


 
/**
 * @author markus
 *
 */
@Path("/file")
public class UploadFileService implements Reader {
	
	private String fileLocation = "";
	private static Logger log = LoggerFactory.getLogger(UploadFileService.class);
	private Model model = ModelFactory.createDefaultModel();

	 
	/**
	 * Upload MULTIPART_FORM_DATA file, save to Java temp directory.
	 * @param stream
	 * @param fileDetail
	 * @return
	 * @throws IOException 
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream stream,
							   @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
		log.debug("File upload service triggered!");

		fileLocation = System.getProperty("java.io.tmpdir") + fileDetail.getFileName();
		writeToFile(stream, fileLocation);
		String output = "File written to " + fileLocation;
 
		return Response.status(200).entity(output).build();
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
				
		fileLocation = System.getProperty("java.io.tmpdir") + fileDetail.getFileName();
		log.debug("File read service triggered!");
		
		model = read(fileLocation);
    	System.out.println("generateModelFromStream().isEmpty(): " + model.isEmpty());

		DataGenerator dataGenerator = new DataGenerator(model);
		 
		return Response.status(200).entity("done").build();
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
		model.read(stream, null, LLProp.getString("tripleInputFormat"));
		log.debug("Read " + model.size() + " elements.");

		return model;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}