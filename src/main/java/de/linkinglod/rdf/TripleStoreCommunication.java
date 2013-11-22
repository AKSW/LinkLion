package de.linkinglod.rdf;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.modify.request.UpdateCreate;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author markus
 *
 */
public class TripleStoreCommunication {

	Model model = ModelFactory.createDefaultModel();
	// TODO pull to prefs
	// TODO remove hard coded file path
	String directory = "/home/markus/lib/fuseki/DB";
	
	/**
	 * @param model
	 */
	public TripleStoreCommunication(Model model) {
		this.model = model;
		// TODO access to dataset, create new one?
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// create TDB/Dataset object
		//Dataset dataset = TDBFactory.createDataset(directory);
		//Model tdb = dataset.getNamedModel("http://linking-lod.eu/");
		
		// load data if needed
		//loadTempData(tdb);

		// write to existing dataset
		//comm.writeToStore(model);

	}

	public void executeQuery(Model tdb, String quString) {
		Query query = QueryFactory.create(quString);
		QueryExecution qexec = QueryExecutionFactory.create(query, tdb);
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			System.out.println("results.next(): " + results.next().toString());
		}		
	}

	public  void loadTempData(Model model) {
		// read from file, load to tdb, data not saved
		// TODO remove hard coded file path
		String source = "/home/markus/Mapping/Links/drugbank-dbpedia.nt.out";
		FileManager.get().readModel( model, source, "TURTLE" );
	}

	public void writeToStore(Model model) {
		HttpContext httpContext = new BasicHttpContext();
		
		UpdateRequest request = UpdateFactory.create();
		request.add(new UpdateCreate("http://linking-lod.eu/graph"));
		UpdateProcessor processor = UpdateExecutionFactory
		    .createRemote(request, "http://localhost:3030/tdb/update");
		((UpdateProcessRemote) processor).setHttpContext(httpContext);
		processor.execute();			
	}

	public void saveModel(Model tsModel) {
		// TODO Auto-generated method stub
		
	}
}
