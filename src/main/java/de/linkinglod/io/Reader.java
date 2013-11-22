package de.linkinglod.io;

import com.hp.hpl.jena.rdf.model.Model;

public interface Reader {
	Model read(String pathToFile);

}
