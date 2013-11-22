package de.linkinglod.io;

import java.io.FileNotFoundException;

import com.hp.hpl.jena.rdf.model.Model;

public interface Reader {
	Model read(String pathToFile) throws FileNotFoundException;

}
