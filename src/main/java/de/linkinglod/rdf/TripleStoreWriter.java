package de.linkinglod.rdf;

import java.util.Iterator;
import java.util.Map;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

import de.linkinglod.io.Writer;

/**
 * Converts a model into triples and uploads them on a Triple Store.
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class TripleStoreWriter implements Writer {

	@Override
	public void write(String graph, Model m, Map<String, String> parameters) {
		
		// connect to Virtuoso instance
		VirtGraph vg = new VirtGraph(graph, parameters.get("TripleStore.localServer"),
				parameters.get("TripleStore.user"), parameters.get("TripleStore.password"));
		
		// convert triples
		Iterator<Statement> it = m.listStatements();
		while(it.hasNext()) {
			Statement s = it.next();
			vg.add(s.asTriple());
		}
		
		// close connection
		vg.close();
		
	}

}
