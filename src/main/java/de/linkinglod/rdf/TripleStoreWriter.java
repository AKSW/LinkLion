package de.linkinglod.rdf;

import java.util.Iterator;
import java.util.Map;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

import de.linkinglod.io.Writer;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class TripleStoreWriter implements Writer {

	private static final String VIRTUOSO_URL = "jdbc:virtuoso://localhost:8890";

	@Override
	public void write(String graph, Model m, Map<String, String> parameters) {
		
		VirtGraph vg = new VirtGraph(graph, VIRTUOSO_URL, parameters.get("user"), parameters.get("password"));

		Iterator<Statement> it = m.listStatements();
		while(it.hasNext()) {
			Statement s = it.next();
			vg.add(s.asTriple());
		}
		
		vg.close();
		
	}

}
