package de.linkinglod.service;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.FormDataMultiPart;
 
/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
@Path("/browse")
public class BrowseMappingService {
	private static Logger log = LoggerFactory.getLogger(UploadFileService.class);
	
	@POST
	@Path("/mapping")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response browseMapping(FormDataMultiPart form) throws IOException, URISyntaxException {
		log.debug("Browse mapping service triggered!");

		String mappingURI = form.getFields().get("mapping-uri").get(0).getValue();
		
		// TODO: Waiting for merging to see what has been done till now.
		
		String out = "";
		
		return Response.status(200).entity(out).build();
	}

}