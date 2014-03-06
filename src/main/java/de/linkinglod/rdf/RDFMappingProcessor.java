package de.linkinglod.rdf;

import java.io.IOException;
import java.util.Date;
import java.util.List;
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
	
	private Model ontoModel = OntologyLoader.getOntModel();
	private Model modelOut = ModelFactory.createDefaultModel();
	private Resource mapping;
	
	private String mappingURI;
	
	// load namespaces
	private String rdf = ontoModel.getNsPrefixURI("rdf");
	private String prov = ontoModel.getNsPrefixURI("prov");
	private String foaf = ontoModel.getNsPrefixURI("foaf");
	private String doap = ontoModel.getNsPrefixURI("doap");
	private String voidVoc = ontoModel.getNsPrefixURI("void");
	private String llont = ontoModel.getNsPrefixURI("llont");
	private String llalg = ontoModel.getNsPrefixURI("llalg");
	private String lllink = ontoModel.getNsPrefixURI("lllink");
	private String lldat = ontoModel.getNsPrefixURI("lldat");
	private String llfw = ontoModel.getNsPrefixURI("llfw");
	private String llmap = ontoModel.getNsPrefixURI("llmap");
	
	// load properties
	private Property rdfSubject = ontoModel.getProperty(rdf + "subject");
	private Property rdfPredicate = ontoModel.getProperty(rdf + "predicate");
	private Property rdfObject = ontoModel.getProperty(rdf + "object");
	private Property wasDerivedFrom = ontoModel.getProperty(prov + "wasDerivedFrom");
	private Property generatedAtTime = ontoModel.getProperty(prov + "generatedAtTime");
	private Property wasGenBy = ontoModel.getProperty(prov + "wasGeneratedBy");
	private Property wasAssocWith = ResourceFactory.createProperty(prov + "wasAssociatedWith");
	private Property isVersionOf = ontoModel.getProperty(prov + "isVersionOf");

	// load individuals/literals (for demo only)
	private Resource algorithm = ontoModel.getResource(llalg + "GenericAlgorithm");
	private Resource sourceDs = ontoModel.getResource(lldat + "GenericDataset-1");
	private Resource targetDs = ontoModel.getResource(lldat + "GenericDataset-2");
	private Resource fwVersion = ontoModel.getResource(llfw + "GenericFramework-1-0");

	// load classes
	private Resource mapClass = ontoModel.getResource(llont + "Mapping");
	private Resource lnkClass = ontoModel.getResource(llont + "Link");
	private Resource algClass = ontoModel.getResource(llont + "Algorithm");
	private Resource fwClass = ontoModel.getResource(llont + "LDFramework");
	private Resource fwvClass = ontoModel.getResource(llont + "LDFrameworkVersion");	
	private Property hasSource = ontoModel.getProperty(llont + "hasSource");
	private Property hasTarget = ontoModel.getProperty(llont + "hasTarget");

	/**
	 * Constructor
	 * @param file
	 * @throws IOException
	 */
	public RDFMappingProcessor(String file) throws IOException {
		String hash = MD5Utils.computeChecksum(file);
		mappingURI = llmap + hash;
		
		// create mapping instance of type Mapping
		mapping = modelOut.createResource(mappingURI, mapClass);
	}

	@Override
	public Model transform(Model modelIn, User owner, Date timeStamp) {
		MD5Utils.reset();
		// copy prefixes (see class Javadoc above)
		modelOut.setNsPrefixes(ontoModel.getNsPrefixMap());
		Literal dateLiteral = modelOut.createTypedLiteral(XMLUtils.toXSD(timeStamp), XSDDatatype.XSDdateTime);
		
		// add mapping properties
		modelOut.add(mapping, generatedAtTime, dateLiteral)
			.add(mapping, wasGenBy, algorithm)
			.add(mapping, hasSource, sourceDs)
			.add(mapping, hasTarget, targetDs)
			.add(algorithm, wasAssocWith, fwVersion);

		// iterate over statements, reify and add to model
		StmtIterator modelIterator = modelIn.listStatements();
		List<Statement> listModel = modelIterator.toList();
		for (Statement statement: listModel) {
			Resource s = statement.getSubject();     
			Property p = statement.getPredicate(); 
			RDFNode o = statement.getObject();

			String linkMD5 = MD5Utils.computeChecksum(s, p, o);
			
			//Resource link = modelOut.createResource(lnkString + linkMD5, lnkClass);
			Resource link = modelOut.createResource(lllink + linkMD5, lnkClass);

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
	 * Create new Resource for a algorithm, add to modelOut, using the linklion ontology and the name, version, url from the form data
	 * @param algName
	 * @param algVersion 
	 * @param algUrl 
	 */
	public void setAlgorithm(String algName, String algVersion, String algUrl) {
		Property foafHomepage = ontoModel.getProperty(foaf + "homepage");
		Property doapVersion = ontoModel.getProperty(doap + "version");
		
		Resource alg = modelOut.createResource(llalg + algName, algClass);
		this.algorithm = alg;

		modelOut.add(alg, foafHomepage, ResourceFactory.createResource(algUrl))
				.add(alg, doapVersion, algVersion);
	}

	public Resource getDataset1() {
		return sourceDs;
	}

	/**
	 * Create new Resource for a source dataset, using the common linklion ontology and the name from the form data
	 * @param name
	 * @param uri
	 * @param type
	 */
	public void setDatasetAndType(String name, String uri, String type) {
		//TODO version for datasets
		Resource dsClass = ontoModel.getResource(voidVoc + "Dataset");
		Property foafPage = ontoModel.getProperty(foaf + "page");
		
		Resource dataset = modelOut.createResource(lldat + name, dsClass);
		if (type.equals("source")) {
			this.sourceDs = dataset;
		}
		else if (type.equals("target")) {
			this.targetDs = dataset;
		}
		
		modelOut.add(dataset, foafPage, uri);	
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
	public void setFramework(String fwName, String fwVersion, String fwUrl) {
		Property foafHomepage = ontoModel.getProperty(foaf + "homepage");
		Property doapRelease = ontoModel.getProperty(doap + "release");
		Property doapRevision = ontoModel.getProperty(doap + "revision");
		
		Resource fw = modelOut.createResource(llfw + fwName, fwClass);
		
		String convertedVersion = convVersToResourceFormat(fwVersion);
		String fwvName = llfw + fwName + convertedVersion;
		Resource fwv = modelOut.createResource(fwvName, fwvClass);
		this.fwVersion = fwv;

		modelOut.add(fw, foafHomepage, ResourceFactory.createResource(fwUrl))
				.add(fw, doapRelease, ResourceFactory.createResource(fwvName))
				.add(fwv, doapRevision, fwVersion)
				.add(fwv, isVersionOf, fw);
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
}
