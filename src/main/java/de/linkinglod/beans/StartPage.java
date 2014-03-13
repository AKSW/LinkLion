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
public class StartPage {
	
	private HashMap<String, String> algorithms;
	private HashMap<String, String> frameworks;
	private HashMap<String, String> datasets;
	
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
	
	public HashMap<String, String> getDatasets() {
		
		datasets = new HashMap<>();
		ArrayList<Dataset> ds = fetchDatasets();
		for(Dataset d : ds)
			datasets.put(d.uri, d.name + " &mdash; " + d.uri);
		return datasets;
		
	}

	private ArrayList<Dataset> fetchDatasets() {
		String query = "select * where { ?x a <http://rdfs.org/ns/void#Dataset> . " +
				"?x <http://rdfs.org/ns/void#uriSpace> ?urispace . " +
				"?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		ArrayList<Dataset> arr = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution n = results.next();
			Resource v = n.getResource("x");
			Literal uri = n.getLiteral("urispace");
			Literal label = n.getLiteral("label");
			arr.add(new Dataset(v.getURI(), label.getString(), uri.getString()));
		}
		return arr;
	}

	private ArrayList<FrameworkVersion> fetchFrameworks() {
		String query = "select * where { ?v a <http://www.linklion.org/ontology#LDFrameworkVersion> . ?f a <http://www.linklion.org/ontology#LDFramework> . " +
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
			arr.add(new FrameworkVersion(v.getURI(), label.getString(), url.getURI(), ver.getString()));
		}
		return arr;
	}

	private ArrayList<Algorithm> fetchAlgorithms() {
		String query = "select * where { ?x a <http://www.linklion.org/ontology#Algorithm> . " +
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
	protected String uri, name, url, version;
	protected FrameworkVersion(String uri, String name, String url, String version) {
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

class Dataset {
	protected String uri, name, uriSpace;
	protected Dataset(String uri, String name, String uriSpace) {
		this.uri = uri;
		this.name = name;
		this.uriSpace = uriSpace;
	}
}
