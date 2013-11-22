package de.linkinglod.service;

import java.io.File;
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
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import de.linkinglod.db.DataGenerator;

 
/**
 * @author markus
 *
 */
@Path("/file")
public class UploadFileService {
	 
	/**
	 * Upload MULTIPART_FORM_DATA file, save to Java temp directory.
	 * @param stream
	 * @param fileDetail
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream stream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) throws NoSuchAlgorithmException {
				
		String fileLocation = System.getProperty("java.io.tmpdir") + fileDetail.getFileName();

		final Logger log = LoggerFactory.getLogger(UploadFileService.class);
		log.debug("File upload service triggered!");
		
		DataGenerator dataGenerator = new DataGenerator(stream, fileLocation, log);
	    
		
		//writeToFile(stream, fileLocation);
		String output = "File *not* uploaded to : " + fileLocation;
 
		// TODO handle error case!?
		return Response.status(200).entity(output).build();
	}
 
	/**
	 * Save uploaded file to specified location.
	 * TODO should we save the file? It's redundant, but easy to download afterwards.
	 * @param stream
	 * @param uploadedFileLocation
	 * @throws IOException 
	 */
	private void writeToFile(InputStream stream, String uploadedFileLocation) throws IOException {

		OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
		int read = 0;
		byte[] bytes = new byte[1024];

		out = new FileOutputStream(new File(uploadedFileLocation));
		while ((read = stream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	} 
}