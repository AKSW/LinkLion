package de.linkinglod.io;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public interface Writer {
	/**
	 * Writes the model.
	 * @param graph Graph
	 * @param m Jena model
	 * @param parameters Parameters
	 */
	void write(String graph, Model m);
}
