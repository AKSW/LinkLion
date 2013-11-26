package de.linkinglod.io;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;

public interface MappingProcessor {
	Set<Property> getLinkTypes();
	List<Set<String>> getNameSpaces();
	Date getTimeStamp();
	String getMappingURI();
	Model transform(Model modelIn, String owner, Date timeStamp);
}