package de.linkinglod.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author markus
 * Generate a set of links (mapping) which can be uploaded to a TripleStore
 * 
 * TODO do we need an extra mapping class like this? Jena RDF model seems to be enough.
 */
public class Mapping {
	
	Model model = ModelFactory.createDefaultModel();
	
	/**
	 * @param model
	 */
	public Mapping(Model model) {
		this.model = model;
		// TODO access to dataset, create new one?
	}

	public void createMapping() {
		
	}
	
	public void uploadMapping() {
		
	}
	
}
