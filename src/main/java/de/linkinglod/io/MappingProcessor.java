package de.linkinglod.io;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;

import de.linkinglod.db.User;

public interface MappingProcessor {
	
	Model transform(Model modelIn, User owner, Date timeStamp);
	
	Set<Property> getLinkTypes();
	
	Map<String, String> getNameSpaces();
	
	Literal getTimeStamp();
	
	String getMappingURI();
}