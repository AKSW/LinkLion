package de.linkinglod.io;

import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

public interface Writer {
	void write(String graph, Model m, Map<String, String> parameters);
}
