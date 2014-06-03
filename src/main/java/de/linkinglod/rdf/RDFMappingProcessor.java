package de.linkinglod.rdf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import de.linkinglod.db.User;
import de.linkinglod.io.MappingProcessor;
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
 * lllink	http://www.linklion.org/link/					<br/>
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
	
	private String fName = "";
	private String dlMappingURI = "http://www.linklion.org/download/mapping/";
	private Model ontoModel = OntologyLoader.getOntModel();
	private Model modelOut = ModelFactory.createDefaultModel();
	private Map<String, String> ns = ontoModel.getNsPrefixMap();
	
	// load properties
	private Property rdfSubject = ontoModel.getProperty(ns.get("rdf") + "subject");
	private Property rdfPredicate = ontoModel.getProperty(ns.get("rdf") + "predicate");
	private Property rdfObject = ontoModel.getProperty(ns.get("rdf") + "object");
	private Property rdfsLabel = ontoModel.getProperty(ns.get("rdfs") + "label");
	private Property wasDerivedFrom = ontoModel.getProperty(ns.get("prov") + "wasDerivedFrom");
	private Property generatedAtTime = ontoModel.getProperty(ns.get("prov") + "generatedAtTime");
	private Property wasGenBy = ontoModel.getProperty(ns.get("prov") + "wasGeneratedBy");
	private Property wasAssocWith = ResourceFactory.createProperty(ns.get("prov") + "wasAssociatedWith");
	private Property hasSource = ontoModel.getProperty(ns.get("llont") + "hasSource");
	private Property hasTarget = ontoModel.getProperty(ns.get("llont") + "hasTarget");
	private Property storedAt = ontoModel.getProperty(ns.get("llont") + "storedAt");

	// load classes
	private Resource mapClass = ontoModel.getResource(ns.get("llont") + "Mapping");
	private Resource lnkClass = ontoModel.getResource(ns.get("llont") + "Link");
	private Resource algClass = ontoModel.getResource(ns.get("llont") + "Algorithm");
	private Resource fwClass = ontoModel.getResource(ns.get("llont") + "LDFramework");
	private Resource fwvClass = ontoModel.getResource(ns.get("llont") + "LDFrameworkVersion");	

	// individuals/literals
	private Resource algorithm;
	private Resource sourceDs;
	private Resource targetDs;
	private Resource fwVersion;
	private Resource mapping;
	private Resource storagePlace;
	private String mappingURI;
	private Literal dateLiteral;

	/**
	 * Constructor
	 * @param mappingHash hash of the mapping which should be uploaded
	 * @param fileName file name without path
	 * @param date 
	 * @param demoUser 
	 * @throws IOException
	 */
	public RDFMappingProcessor(String mappingHash, String fileName, User owner, Date timeStamp) throws IOException {
		
		fName = fileName;
		storagePlace = modelOut.createResource(dlMappingURI + fName);

		mappingURI = ns.get("llmap") + mappingHash;
		mapping = modelOut.createResource(mappingURI, mapClass);
		
		dateLiteral = modelOut.createTypedLiteral(XMLUtils.toXSD(timeStamp), XSDDatatype.XSDdateTime);
	}

	@Override
	public Model transform(Model modelIn) {
		MD5Utils.reset();
		// copy prefixes (see class Javadoc above)
		modelOut.setNsPrefixes(ontoModel.getNsPrefixMap());
		
		// add mapping properties
		modelOut.add(mapping, generatedAtTime, dateLiteral)
			.add(mapping, wasGenBy, algorithm)
			.add(mapping, hasSource, sourceDs)
			.add(mapping, hasTarget, targetDs)
			.add(algorithm, wasAssocWith, fwVersion)
			.add(mapping, storedAt, storagePlace);

		// iterate over statements, reify and add to model
		StmtIterator modelIterator = modelIn.listStatements();
		List<Statement> listModel = modelIterator.toList();
		for (Statement statement: listModel) {
			Resource s = statement.getSubject();     
			Property p = statement.getPredicate(); 
			RDFNode o = statement.getObject();

			String linkMD5 = MD5Utils.computeChecksum(s, p, o);
			
			Resource link = modelOut.createResource(ns.get("lllink") + linkMD5, lnkClass);

			modelOut.add(link, rdfSubject, s)
				.add(link, rdfPredicate, p)
				.add(link, rdfObject, o)
				.add(link, wasDerivedFrom, mapping);
		}
		
		return modelOut;
	}

	@Override
	public Set<Property> getLinkTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getNameSpaces() {
		return ns;
	}

	@Override
	public Literal getTimeStamp() {
		return dateLiteral;
	}

	@Override
	public String getMappingURI() {
		return mappingURI;
	}
	
	public Resource getAlgorithm() {
		return algorithm;
	}

	/**
	 * Create new Resource for a algorithm, add to modelOut, using the linklion ontology and the name, version, url from the form data
	 * @param algName
	 * @param algVersion 
	 * @param algUrl 
	 */
	public void addNewAlgorithm(String algName, String algUrl) {
		Property foafHomepage = ontoModel.getProperty(ns.get("foaf") + "homepage");
		
		Resource alg = modelOut.createResource(ns.get("llalg") + encodeURI(algName), algClass);
		this.algorithm = alg;

		modelOut.add(alg, foafHomepage, ResourceFactory.createResource(algUrl))
				.add(alg, rdfsLabel, algName);
	}

	public Resource getSourceDataset() {
		return sourceDs;
	}

	public Resource getTargetDataset() {
		return targetDs;
	}

	private String encodeURI(String uri) {
		try {
			return URLEncoder.encode(uri.replaceAll(" ", "_"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never happens because encoding is a constant
			return null;
		}
	}
	
	/**
	 * Create new Resource for a dataset, using the common linklion ontology and the name from the form data
	 * @param name
	 * @param urisp
	 * @param type
	 */
	public void addNewDataset(String name, String urisp, String type) {
		//TODO version for datasets
		Resource dsClass = ontoModel.getResource(ns.get("void") + "Dataset");
		Property urispace = ontoModel.getProperty(ns.get("void") + "uriSpace");
		
		Resource dataset = modelOut.createResource(ns.get("lldat") + encodeURI(name), dsClass);
		if (type.equals("source")) {
			this.sourceDs = dataset;
		} else { // "target"
			this.targetDs = dataset;
		}
		
		modelOut.add(dataset, urispace, urisp)
				.add(dataset, rdfsLabel, name);	
	}
	
	public Resource getFramework() {
		return fwVersion;
	}

	/**
	 * Create new Resource for a framework, add to modelOut, using the linklion ontology and the name, url, version from the form data
	 * @param fwName
	 * @param fwUrl 
	 * @param fwVersion 
	 */
	public void addNewFramework(String fwName, String fwVersion, String fwUrl) {
		Property foafHomepage = ontoModel.getProperty(ns.get("foaf") + "homepage");
		Property doapRelease = ontoModel.getProperty(ns.get("doap") + "release");
		Property doapRevision = ontoModel.getProperty(ns.get("doap") + "revision");
		
		Resource fw = modelOut.createResource(ns.get("llfw") + encodeURI(fwName), fwClass);
		
		String convertedVersion = convVersToResourceFormat(fwVersion);
		String fwvName = fwName + " " + fwVersion;
		Resource fwv = modelOut.createResource(ns.get("llver") + encodeURI(fwName) + convertedVersion, fwvClass);
		
		this.fwVersion = fwv;

		modelOut.add(fw, foafHomepage, ResourceFactory.createResource(fwUrl))
				.add(fw, rdfsLabel, fwName)
				.add(fw, doapRelease, fwv)
				.add(fwv, doapRevision, fwVersion)
				.add(fwv, rdfsLabel, fwvName);
	}

	/**
	 * Return all numbers contained in a string separated by -, insert starting -, omit trailing -
	 * TODO support filtering of chars which are not allowed in urls
	 * @param fwVersion
	 * @return result
	 */
	private String convVersToResourceFormat(String fwVersion) {
		String result =  fwVersion.replaceAll("[^0-9]","-");
		result = result.replaceAll("[-]+", "-");
				
		if (!result.startsWith("-")) {
			result = "-" + result;	
		}
		if (result.endsWith("-")) {
			result = result.substring(0, result.length()-1);
		}
		
		return result;
	}

	public void setFramework(String fwURI) {
		Resource fwv = ResourceFactory.createResource(fwURI);
		this.fwVersion = fwv;
	}

	public void setAlgorithm(String algURI) {
		Resource alg = ResourceFactory.createResource(algURI);
		this.algorithm = alg;
	}

	public void setSourceDataset(String dsURI) {
		Resource ds = ResourceFactory.createResource(dsURI);
		this.sourceDs = ds;
	}

	public void setTargetDataset(String dsURI) {
		Resource ds = ResourceFactory.createResource(dsURI);
		this.targetDs = ds;
	}
}
