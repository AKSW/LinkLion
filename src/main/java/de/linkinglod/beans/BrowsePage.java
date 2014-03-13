package de.linkinglod.beans;

import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

import de.linkinglod.service.LLProp;

/**
 * TODO arrange everything without using protected classes
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class BrowsePage {

	private HashMap<String, String> mappings;
	
	private String endpoint = LLProp.getString("TripleStore.endpoint");
	private String graph = LLProp.getString("TripleStore.graph");

	public HashMap<String, String> getMappings() {
		
		mappings = new HashMap<>();
		ArrayList<Mapping> mp = fetchMappings();
		for(Mapping m : mp)
			mappings.put(m.uri, m.srcName + " &mdash; " + m.tgtName);
		return mappings;
		
	}
	
	private ArrayList<Mapping> fetchMappings() {
		String query = "select * where {" +
				"?x a <http://www.linklion.org/ontology#Mapping> ." +
				"?x <http://www.linklion.org/ontology#hasSource> ?s ." +
				"?x <http://www.linklion.org/ontology#hasTarget> ?t ." +
				"?s <http://www.w3.org/2000/01/rdf-schema#label> ?src ." +
				"?t <http://www.w3.org/2000/01/rdf-schema#label> ?tgt" +
				"}";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		ArrayList<Mapping> arr = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution n = results.next();
			Resource m = n.getResource("x");
			Literal src = n.getLiteral("src");
			Literal tgt = n.getLiteral("tgt");
			arr.add(new Mapping(m.getURI(), src.getString(), tgt.getString()));
		}
		return arr;
	}

}

class Mapping {
	protected String uri, srcName, tgtName;
	protected Mapping(String uri, String srcName, String tgtName) {
		this.uri = uri;
		this.srcName = srcName;
		this.tgtName = tgtName;
	}
}
