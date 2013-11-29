package de.linkinglod.rdf;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import de.linkinglod.db.User;
import de.linkinglod.io.MappingProcessor;
import de.linkinglod.service.LLProp;
import de.linkinglod.service.OntologyLoader;
import de.linkinglod.util.MD5Utils;

/**
 * Prefixes are:<br/>
 * rdf		http://www.w3.org/1999/02/22-rdf-syntax-ns#		<br/>
 * rdfs		http://www.w3.org/2000/01/rdf-schema#			<br/>
 * owl		http://www.w3.org/2002/07/owl#					<br/>
 * xsd		http://www.w3.org/2001/XMLSchema#				<br/>
 * foaf		http://xmlns.com/foaf/0.1/						<br/>
 * prov		http://www.w3.org/ns/prov#						<br/>
 * void		http://rdfs.org/ns/void#						<br/>
 * doap		http://usefulinc.com/ns/doap#					<br/>
 * dcterms	http://purl.org/dc/terms/						<br/>
 * llont	http://www.linklion.org/ontology#				<br/>
 * llalg	http://www.linklion.org/algorithm/				<br/>
 * lldat	http://www.linklion.org/dataset/				<br/>
 * llfw		http://www.linklion.org/framework/				<br/>
 * llmap	http://www.linklion.org/mapping/				<br/>
 * llver	http://www.linklion.org/version/				<br/>
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Markus Nentwig
 *
 */
public class RDFMappingProcessor implements MappingProcessor {
	
	private String mappingURI;
	
	public RDFMappingProcessor(String file) throws IOException {
		// TODO: for now, mapping hash is MD5(file_content)
		String hash = MD5Utils.computeChecksum(file);
		String ns = LLProp.getString("ns");
		String lim = LLProp.getString("delimiter");
		String vocMap = LLProp.getString("vocabularyMapping");
		mappingURI = ns + lim + vocMap + lim + hash;
	}

	@Override
	public Model transform(Model modelIn, User owner, Date timeStamp) {
		
		// prepare
		Model ontoModel = OntologyLoader.getOntModel();
		Model modelOut = ModelFactory.createDefaultModel();
		MD5Utils.reset();
		String ns = LLProp.getString("ns");
		String lim = LLProp.getString("delimiter");
		
		// copy prefixes (see class Javadoc above)
		modelOut.setNsPrefixes(ontoModel.getNsPrefixMap());
		
		// load namespaces
		String rdf = ontoModel.getNsPrefixURI("rdf");
		String prov = ontoModel.getNsPrefixURI("prov");
		String llont = ontoModel.getNsPrefixURI("llont");
		String llalg = ontoModel.getNsPrefixURI("llalg");
		String lldat = ontoModel.getNsPrefixURI("lldat");
		
		// load properties
		Property propS = ontoModel.getProperty(rdf + "subject");
		Property propP = ontoModel.getProperty(rdf + "predicate");
		Property propO = ontoModel.getProperty(rdf + "object");
		Property propM = ontoModel.getProperty(prov + "wasDerivedFrom");
		Property genAt = ontoModel.getProperty(prov + "generatedAtTime");
		Property wasGenBy = ontoModel.getProperty(prov + "wasGeneratedBy");
		Property hasSource = ontoModel.getProperty(llont + "hasSource");
		Property hasTarget = ontoModel.getProperty(llont + "hasTarget");
		
		// load individuals/literals (for demo only)
		Resource algorithm = ontoModel.getResource(llalg + "GenericAlgorithm");
		Resource dataset1 = ontoModel.getResource(lldat + "GenericDataset-1");
		Resource dataset2 = ontoModel.getResource(lldat + "GenericDataset-2");
		Literal dateLiteral = modelOut.createTypedLiteral(toXSD(timeStamp), XSDDatatype.XSDdateTime);
		
		// load classes
		Resource mapClass = ontoModel.getResource(llont + "Mapping");
		Resource lnkClass = ontoModel.getResource(llont + "Link");
		
		// create mapping instance of type Mapping
		Resource mapping = modelOut.createResource(getMappingURI(), mapClass);

		// add mapping properties
		modelOut.add(mapping, genAt, dateLiteral)
			.add(mapping, wasGenBy, algorithm)
			.add(mapping, hasSource, dataset1)
			.add(mapping, hasTarget, dataset2);

		// link namespace
		String lnkString = ns + lim + LLProp.getString("vocabularyLink") + lim;

		// iterate over statements, reify and add to model
		Iterator<Statement> it = modelIn.listStatements();
		while(it.hasNext()) {
			Statement statement = it.next();
			Resource s = statement.getSubject();     
			Property p = statement.getPredicate(); 
			RDFNode o = statement.getObject();

			String linkMD5 = MD5Utils.computeChecksum(s, p, o);
			
			Resource link = modelOut.createResource(lnkString + linkMD5, lnkClass);

			modelOut.add(link, propS, s)
				.add(link, propP, p)
				.add(link, propO, o)
				.add(link, propM, mapping);
		}
		
		// converted model
		return modelOut;
	}

	/**
	 * Converts java Date class into XSDdateTime format.
	 * @param timeStamp
	 * @return
	 */
	private String toXSD(Date timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(timeStamp);
	}

	@Override
	public Set<Property> getLinkTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Set<String>> getNameSpaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getTimeStamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMappingURI() {
		return mappingURI;
	}

}
