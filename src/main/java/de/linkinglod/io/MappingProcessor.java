package de.linkinglod.io;

import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;

public interface MappingProcessor {
	
	Model transform(Model modelIn);
	
	Set<Property> getLinkTypes();
	
	Map<String, String> getNameSpaces();
	
	Literal getTimeStamp();
	
	String getMappingURI();
}