package de.linkinglod.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import de.linkinglod.db.User;
import de.linkinglod.rdf.RDFMappingProcessor;

/**
 * Load the ontology as a Jena model. Jena doesn't handle OWL files, so the ontology has to be saved in RDF format.
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class OntologyLoader {

	private static String ontoFile = "ld-portal-ontology.rdf";
	private static Model ontModel;
	private static Logger log = LoggerFactory.getLogger(OntologyLoader.class);
	
	static {
		try {
			InputStream in = OntologyLoader.class.getResourceAsStream("/" + ontoFile);
			ontModel = ModelFactory.createDefaultModel();
			ontModel.read(in, "RDF/XML");
			in.close();
		} catch (IOException e) {
			log.debug("Missing OWL Ontology!");
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	public static Model getOntModel() {
		return ontModel;
	}

	/**
	 * For tests only. Remove '/' in path.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty( "user.dir" ));
		
		// test for OntologyLoader 
		Model ontoModel = getOntModel();
		Iterator<Statement> it = ontoModel.listStatements();
		while(it.hasNext())
			System.out.println(it.next());
		
		// test for RDFMappingProcessor
		System.out.println(" --- MODEL OUT --- ");
		RDFMappingProcessor rmp = new RDFMappingProcessor(ontoFile);
		Model m2 = rmp.transform(ModelFactory.createDefaultModel(), new User(), new Date());
		m2.add(ontoModel);
		Iterator<Statement> it2 = m2.listStatements();
		while(it2.hasNext())
			System.out.println(it2.next());
		
	}

}
