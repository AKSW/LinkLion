package de.linkinglod.rdf;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.sun.jersey.multipart.FormDataBodyPart;

import de.linkinglod.db.User;
import de.linkinglod.io.MappingProcessor;
import de.linkinglod.service.LLProp;
import de.linkinglod.service.OntologyLoader;
import de.linkinglod.util.MD5Utils;
import de.linkinglod.util.XMLUtils;

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
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 *
 */
public class RDFMappingProcessor implements MappingProcessor {
	
	private Model ontoModel = OntologyLoader.getOntModel();
	private Model modelOut = ModelFactory.createDefaultModel();
	
	private String mappingURI;
	private String ns = LLProp.getString("ns");
	private String lim = LLProp.getString("delimiter");
	private String vocMap = LLProp.getString("vocabularyMapping");
	
	// load namespaces
	private String rdf = ontoModel.getNsPrefixURI("rdf");
	private String prov = ontoModel.getNsPrefixURI("prov");
	private String llont = ontoModel.getNsPrefixURI("llont");
	private String llalg = ontoModel.getNsPrefixURI("llalg");
	private String lldat = ontoModel.getNsPrefixURI("lldat");
	private String llfw = ontoModel.getNsPrefixURI("llfw");
	
	// load properties
	private Property propS = ontoModel.getProperty(rdf + "subject");
	private Property propP = ontoModel.getProperty(rdf + "predicate");
	private Property propO = ontoModel.getProperty(rdf + "object");
	private Property propM = ontoModel.getProperty(prov + "wasDerivedFrom");
	private Property genAt = ontoModel.getProperty(prov + "generatedAtTime");
	private Property wasGenBy = ontoModel.getProperty(prov + "wasGeneratedBy");
	private Property wasAssocWith = ResourceFactory.createProperty(prov + "wasAssociatedWith");

	// load individuals/literals (for demo only)
	private Resource algorithm = ontoModel.getResource(llalg + "GenericAlgorithm");
	private Resource dataset1 = ontoModel.getResource(lldat + "GenericDataset-1");
	private Resource dataset2 = ontoModel.getResource(lldat + "GenericDataset-2");
	private Resource framework = ontoModel.getResource(llfw + "GenericFramework-1-0");

	// load classes
	private Resource mapClass = ontoModel.getResource(llont + "Mapping");
	private Resource lnkClass = ontoModel.getResource(llont + "Link");
	
	// create mapping instance of type Mapping
	private Resource mapping = modelOut.createResource(getMappingURI(), mapClass);
	
	private Property hasSource = ontoModel.getProperty(llont + "hasSource");
	private Property hasTarget = ontoModel.getProperty(llont + "hasTarget");

	// link namespace
	String lnkString = ns + lim + LLProp.getString("vocabularyLink") + lim;
	
	/**
	 * Constructor
	 * @param file
	 * @throws IOException
	 */
	public RDFMappingProcessor(String file) throws IOException {
		String hash = MD5Utils.computeChecksum(file);
		mappingURI = ns + lim + vocMap + lim + hash;
	}

	@Override
	public Model transform(Model modelIn, User owner, Date timeStamp) {
		MD5Utils.reset();
		// copy prefixes (see class Javadoc above)
		modelOut.setNsPrefixes(ontoModel.getNsPrefixMap());
		Literal dateLiteral = modelOut.createTypedLiteral(XMLUtils.toXSD(timeStamp), XSDDatatype.XSDdateTime);

		// add mapping properties
		modelOut.add(mapping, genAt, dateLiteral)
			.add(mapping, wasGenBy, algorithm)
			.add(mapping, hasSource, dataset1)
			.add(mapping, hasTarget, dataset2)
			.add(algorithm, wasAssocWith, framework);

		// iterate over statements, reify and add to model
		StmtIterator modelIterator = modelIn.listStatements();
		List<Statement> listModel = modelIterator.toList();
		for (Statement statement: listModel) {
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
		
		return modelOut;
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
	
	public Resource getAlgorithm() {
		return algorithm;
	}

	/**
	 * Create new Resource for a algorithm, using the common linklion ontology and the name from the form data
	 * @param algName
	 */
	public void setAlgorithm(String algName) {
		this.algorithm = ResourceFactory.createResource(llalg + algName);
	}

	public Resource getDataset1() {
		return dataset1;
	}

	/**
	 * Create new Resource for a source dataset, using the common linklion ontology and the name from the form data
	 * @param sourceName
	 */
	public void setDataset1(String sourceName) {
		this.dataset1 = ResourceFactory.createResource(lldat + sourceName);
	}

	public Resource getDataset2() {
		return dataset2;
	}

	/**
	 * Create new Resource for a target dataset, using the common linklion ontology and the name from the form data
	 * @param targetName
	 */
	public void setDataset2(String targetName) {
		this.dataset2 = ResourceFactory.createResource(lldat + targetName);
	}
	
	public Resource getFramework() {
		return framework;
	}

	/**
	 * Create new Resource for a framework, using the common linklion ontology and the name from the form data
	 * @param fwName
	 */
	public void setFramework(String fwName) {
		this.framework = ResourceFactory.createResource(llfw + fwName);
	}

}
