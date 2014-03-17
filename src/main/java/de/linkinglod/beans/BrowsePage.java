package de.linkinglod.beans;

import java.util.ArrayList;
import java.util.List;

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

	// hash map of all available mappings
	private List<Mapping> mappings;
	// selected mapping
//	private String mappingURI;
	
//	private List<Link> linksByMapping;
	
	private String endpoint = LLProp.getString("TripleStore.endpoint");
	private String graph = LLProp.getString("TripleStore.graph");

	public List<Mapping> getMappings() {
		
		mappings = fetchMappings();
		return mappings;
		
	}
	
//	public List<Link> getLinksByMapping() {
//		linksByMapping = new ArrayList<Link>();
//		
//		String query = "select * where {" +
//				"?x a <http://www.linklion.org/ontology#Link> ." +
//				"?x <http://www.w3.org/ns/prov#wasDerivedFrom> <http://www.linklion.org/mapping/"+mappingURI+"> . " +
//				"?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> ?s . " +
//				"?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> ?o . " +
//				"}";
//		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
//		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
//		ResultSet results = qexec.execSelect();
//		while (results.hasNext()) {
//			QuerySolution n = results.next();
//			Resource x = n.getResource("x");
//			Literal s = n.getLiteral("s");
//			Literal o = n.getLiteral("o");
//			linksByMapping.add(new Link(x.getURI(), s.getString(), o.getString()));
//		}
//		
//		return linksByMapping;
//	}

	private ArrayList<Mapping> fetchMappings() {
		String query = "select * where {" +
				"?x a <http://www.linklion.org/ontology#Mapping> ." +
				"?x <http://www.linklion.org/ontology#hasSource> ?s ." +
				"?x <http://www.linklion.org/ontology#hasTarget> ?t ." +
				"?x <http://www.linklion.org/ontology#storedAt> ?store ." +
				"?s <http://www.w3.org/2000/01/rdf-schema#label> ?src ." +
				"?t <http://www.w3.org/2000/01/rdf-schema#label> ?tgt" +
				"} ORDER BY ?src ?tgt";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		ArrayList<Mapping> arr = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution n = results.next();
			Resource m = n.getResource("x");
			Literal src = n.getLiteral("src");
			Literal tgt = n.getLiteral("tgt");
			Resource store = n.getResource("store");
			arr.add(new Mapping(m.getURI(), src.getString(), tgt.getString(), store.getURI()));
		}
		return arr;
	}

//	public String getMappingURI() {
//		return mappingURI;
//	}
//
//	public void setMappingURI(String mappingURI) {
//		this.mappingURI = mappingURI;
//	}

}


//class Link {
//	private String uri, source, target;
//	public Link(String uri, String source, String target) {
//		this.setUri(uri);
//		this.setSource(source);
//		this.setTarget(target);
//	}
//	public String getUri() {
//		return uri;
//	}
//	public void setUri(String uri) {
//		this.uri = uri;
//	}
//	public String getSource() {
//		return source;
//	}
//	public void setSource(String source) {
//		this.source = source;
//	}
//	public String getTarget() {
//		return target;
//	}
//	public void setTarget(String target) {
//		this.target = target;
//	}
//}
