package de.linkinglod.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 */
public class BrowsePage {

	// hash map of all available mappings
	private List<Mapping> mappings;
	private List<RsDataset> datasets;
	
	private String endpoint = LLProp.getString("TripleStore.endpoint");
	private String graph = LLProp.getString("TripleStore.graph");

	public List<Mapping> getMappings() {
		
		mappings = fetchMappings();
		HashMap<String, Integer> lpm = linksPerMapping();
		for(Mapping m : mappings) {
			Integer n = lpm.get(m.getUri());
			if(n != null)
				m.setNumLinks(n);
			else
				m.setNumLinks(0);
		}
		return mappings;
	}
	
	public List<RsDataset> getDatasets() {
		datasets = fetchMappingsPerDataset();
		
		// mapping, ds
		HashMap<String, HashMap<String, String>> distinctDsMa = fetchDistinctMappingDataset();
		// mapping, lcount
		HashMap<String, Integer> linksPerMapping = linksPerMapping();
		// ds, lcount
		HashMap<String, Integer> linksPerDs = new HashMap<>();
		for (Map.Entry<String, Integer> lpmEntry : linksPerMapping.entrySet()) {
			String lpmKey = lpmEntry.getKey();
			HashMap<String, String> st = distinctDsMa.get(lpmKey);
			String source = st.keySet().iterator().next();
			String target = st.values().iterator().next();
						
			if (linksPerDs.containsKey(source)) {
				int value = linksPerDs.get(source) + linksPerMapping.get(lpmKey); 
				linksPerDs.put(source, value);
			}
			else {
				linksPerDs.put(source, linksPerMapping.get(lpmKey));
			}
			
			if (linksPerDs.containsKey(target)) {
				int value = linksPerDs.get(target) + linksPerMapping.get(lpmKey); 
				linksPerDs.put(target, value);
			}
			else {
				linksPerDs.put(target, linksPerMapping.get(lpmKey));
			}
		}
		for (RsDataset ds: datasets) {
			Integer n = linksPerDs.get(ds.getLlUri());
			if(n != null)
				ds.setlCount(n);
			else
				ds.setlCount(0);
		}

		return datasets;
		
	}
	
	private HashMap<String, HashMap<String, String>> fetchDistinctMappingDataset() {
		String query = "select distinct * where {" +
				"?m a <http://www.linklion.org/ontology#Mapping> ." +
				"?m <http://www.linklion.org/ontology#hasSource> ?s ." +
				"?m <http://www.linklion.org/ontology#hasTarget> ?t ."
				+ " } ORDER BY ?m ?s ?t";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		while (results.hasNext()) {
			HashMap<String, String> st = new HashMap<String, String>();
			QuerySolution n = results.next();
			Resource mapping = n.getResource("m");
			Resource s = n.getResource("s");
			Resource t = n.getResource("t");
			st.put(s.getURI(), t.getURI());
			map.put(mapping.getURI(), st);
		}
		return map;
	}
	
	private ArrayList<RsDataset> fetchMappingsPerDataset() {
                System.out.println("fetchMappingsPerDataset()");
                String query = "select ?ds ?label ?uri (count(?m) as ?mcount) where " +
                		" { " + 
                		" ?ds <http://www.w3.org/2000/01/rdf-schema#label> ?label . " +
                		" ?ds <http://rdfs.org/ns/void#uriSpace> ?uri .  " +
                		" ?ds a <http://rdfs.org/ns/void#Dataset> .  " +
                		" { ?m <http://www.linklion.org/ontology#hasSource> ?ds . }  " +
                		" UNION  " +
                		" { ?m <http://www.linklion.org/ontology#hasTarget> ?ds . }  " +
                		" } GROUP BY ?ds ?label ?uri ORDER BY desc(count(?m))";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();		
		ArrayList<RsDataset> arr = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution n = results.next();
			Literal mCount = n.getLiteral("mcount");
			String mString = mCount.toString();
			int number = Integer.parseInt(mString.substring(0, mString.indexOf("^")));
			Resource llUri = n.getResource("ds");
			Literal label = n.getLiteral("label");
			Resource uri = n.getResource("uri");
			arr.add(new RsDataset(uri.getURI(), label.toString(), number, llUri.getURI()));
		}
		return arr;
	}

	private ArrayList<Mapping> fetchMappings() {
		String query = "select * where {" +
				"?x a <http://www.linklion.org/ontology#Mapping> ." +
				"?x <http://www.linklion.org/ontology#hasSource> ?s ." +
				"?x <http://www.linklion.org/ontology#hasTarget> ?t ." +
				"?x <http://www.linklion.org/ontology#storedAt> ?store ." +
				"}";
//				"?s <http://www.w3.org/2000/01/rdf-schema#label> ?src ." +
//				"?t <http://www.w3.org/2000/01/rdf-schema#label> ?tgt" +
//				"} ORDER BY ?src ?tgt";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		ArrayList<Mapping> arr = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution n = results.next();
			Resource m = n.getResource("x");
			// TODO restore after having reinstalled Virtuoso
//			Literal src = n.getLiteral("src");
//			Literal tgt = n.getLiteral("tgt");
			Resource store = n.getResource("store");
			String s = n.getResource("s").getURI();
			String t = n.getResource("t").getURI();
			String src1 = s.substring(s.lastIndexOf("/")+1);
			String tgt1 = t.substring(t.lastIndexOf("/")+1);
			arr.add(new Mapping(m.getURI(), src1, tgt1, store.getURI()));
		}
		return arr;
	}
	
	private HashMap<String, Integer> linksPerMapping() {
		HashMap<String, Integer> lpm = new HashMap<>();
		String query = "select ?x (count(?l) as ?links) where { " +
				"?x a <http://www.linklion.org/ontology#Mapping> . " +
				"?l <http://www.w3.org/ns/prov#wasDerivedFrom> ?x " +
				"} GROUP BY ?x";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			QuerySolution n = results.next();
			Resource x = n.getResource("x");
			Literal links = n.getLiteral("links");
			lpm.put(x.getURI(), links.getInt());
		}
		return lpm;
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
