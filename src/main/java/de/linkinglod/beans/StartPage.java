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
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class StartPage {
	
	private HashMap<String, String> algorithms;
	private HashMap<String, String> frameworks;
	
	private String endpoint = LLProp.getString("TripleStore.endpoint");
	private String graph = LLProp.getString("TripleStore.graph");

	public HashMap<String, String> getFrameworks() {
		
		frameworks = new HashMap<>();
		
		ArrayList<FrameworkVersion> fw = fetchFrameworks();
		
		for(FrameworkVersion f : fw)
			frameworks.put(f.uri, f.name + " &mdash; " + f.url);
		
		return frameworks;
		
	}
	
	public HashMap<String, String> getAlgorithms() {
		
		algorithms = new HashMap<>();
		
		ArrayList<Algorithm> al = fetchAlgorithms();
		
		for(Algorithm a : al)
			algorithms.put(a.uri, a.name + " &mdash; " + a.url);

		return algorithms;
		
	}
	
	private ArrayList<FrameworkVersion> fetchFrameworks() {
		String query = "select * where { ?v a <http://www.linklion.org/ontology/LDFrameworkVersion> . ?f a <http://www.linklion.org/ontology/LDFramework> . " +
				"?f <http://usefulinc.com/ns/doap#release> ?v . ?f <http://xmlns.com/foaf/0.1/homepage> ?url . " +
				"?v <http://www.w3.org/2000/01/rdf-schema#label> ?label . ?v <http://usefulinc.com/ns/doap#revision> ?ver }";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		ArrayList<FrameworkVersion> arr = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution n = results.next();
			Resource v = n.getResource("v");
			Resource url = n.getResource("url");
			Literal label = n.getLiteral("label");
			Literal ver = n.getLiteral("ver");
			arr.add(new FrameworkVersion(v.getURI(), label.getString(), url.getURI(), ver.getFloat()));
		}
		return arr;
	}

	private ArrayList<Algorithm> fetchAlgorithms() {
		String query = "select * where { ?x a <http://www.linklion.org/ontology/Algorithm> . " +
				"?x <http://xmlns.com/foaf/0.1/homepage> ?url . " +
				"?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		ArrayList<Algorithm> arr = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution n = results.next();
			Resource v = n.getResource("x");
			Resource url = n.getResource("url");
			Literal label = n.getLiteral("label");
			arr.add(new Algorithm(v.getURI(), label.getString(), url.getURI()));
		}
		return arr;
	}
	
}

class FrameworkVersion {
	protected String uri, name, url;
	protected float version;
	protected FrameworkVersion(String uri, String name, String url, float version) {
		this.uri = uri;
		this.name = name;
		this.url = url;
		this.version = version;
	}
}

class Algorithm {
	protected String uri, name, url;
	protected Algorithm(String uri, String name, String url) {
		this.uri = uri;
		this.name = name;
		this.url = url;
	}
}
