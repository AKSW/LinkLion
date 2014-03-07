package de.linkinglod.rdf;

import java.util.Iterator;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import de.linkinglod.io.Writer;
import de.linkinglod.service.LLProp;

/**
 * Converts a model into triples and uploads them on a Triple Store.
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class TripleStoreWriter implements Writer {

	@Override
	public void write(String graph, Model m) {
		
		String localServer = LLProp.getString("TripleStore.localServer");
		String user = LLProp.getString("TripleStore.user");
		String password = LLProp.getString("TripleStore.password");
		
		// connect to Virtuoso instance
		VirtGraph vg = new VirtGraph(graph, localServer, user, password);
		
		// convert triples
		Iterator<Statement> it = m.listStatements();
		while(it.hasNext()) {
			Statement s = it.next();
			vg.add(s.asTriple());
		}
		
		// close connection
		vg.close();
		
	}
	
	/**
	 * Test with some dummy data.
	 * @param args
	 */
	public static void main(String[] args) {
		Model m = ModelFactory.createDefaultModel();
		m.add(ResourceFactory.createResource("http://localhost"),
				ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/name"),
				"localhost");
		TripleStoreWriter tsw = new TripleStoreWriter();
		tsw.write(LLProp.getString("TripleStore.graph"), m);
	}

}
