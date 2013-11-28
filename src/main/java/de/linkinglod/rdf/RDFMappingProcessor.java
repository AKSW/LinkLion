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
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import de.linkinglod.db.User;
import de.linkinglod.io.MappingProcessor;
import de.linkinglod.service.LLProp;
import de.linkinglod.util.MD5Utils;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Markus Nentwig
 *
 */
public class RDFMappingProcessor implements MappingProcessor {
	
	String mappingURI;
	
	public RDFMappingProcessor(String file) throws IOException {
		// TODO: for now, mapping hash is MD5(file_content)
		String hash = MD5Utils.computeChecksum(file);
		String ns = LLProp.getString("ns");
		String lim = LLProp.getString("delimiter");
		String vocProp = LLProp.getString("vocabularyLink");
		mappingURI = ns + lim + vocProp + lim + hash;
	}

	@Override
	public Model transform(Model modelIn, User owner, Date timeStamp) {
		
		// prepare
		Model modelOut = ModelFactory.createDefaultModel();
		MD5Utils.reset();
		String ns = LLProp.getString("ns");
		String lim = LLProp.getString("delimiter");
		String fi = LLProp.getString("fragmentIdentifier");
		String vocOnt = LLProp.getString("vocabularyOntology");
		String vocProp = LLProp.getString("vocabularyProperty");
		String vocAlg = LLProp.getString("vocabularyOntology");
		String vocFw = LLProp.getString("vocabularyFramework");
		String vocVer = LLProp.getString("vocabularyVersion");
		String ontString = ns + lim + vocOnt + fi;
		String propString = ns + lim + vocProp + lim;
		String algString = ns + lim + vocAlg + lim;
		String fwString = ns + lim + vocFw + lim;
		String verString = ns + lim + vocVer + lim;
		String preMd5 = ns + lim + LLProp.getString("vocabularyLink") + fi;
		Property propS = ResourceFactory.createProperty(propString + LLProp.getString("subjectAttribute"));
		Property propP = ResourceFactory.createProperty(propString + LLProp.getString("linkType"));
		Property propO = ResourceFactory.createProperty(propString + LLProp.getString("objectAttribute"));
		Property propM = ResourceFactory.createProperty(propString + LLProp.getString("hashMapping"));
		
		// TODO load OWL ontology
		Resource mapping = modelOut.createResource(getMappingURI(), ResourceFactory.createResource(ontString + "Mapping"));
		Resource algorithm = modelOut.createResource(algString + "GenericAlgorithm", ResourceFactory.createResource(ontString + "Algorithm"));
		Resource versionedLDF = modelOut.createResource(fwString + "GenericFramework1-0", ResourceFactory.createResource(ontString + "VersionedLinkDiscoveryFramework"));
		Resource version = modelOut.createResource(verString + "GenericFrameworkVersion1-0", ResourceFactory.createResource("http://usefulinc.com/ns/doap#Version"));
		Resource theLDF = modelOut.createResource(fwString + "GenericFramework", ResourceFactory.createResource(ontString + "LinkDiscoveryFramework"));
		
		Property genAt = ResourceFactory.createProperty("http://www.w3.org/ns/prov#generatedAtTime");
		Literal dateLiteral = modelOut.createTypedLiteral(toXSD(timeStamp), XSDDatatype.XSDdateTime);
		Property wasGenBy = ResourceFactory.createProperty(propString + "wasGeneratedBy");
		Property wasAssoc = ResourceFactory.createProperty(propString + "wasAssociatedWith");
		Property isVers = ResourceFactory.createProperty(propString + "isVersionOf");
		Property name = ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/name");
		Property release = ResourceFactory.createProperty("http://usefulinc.com/ns/doap#release");
		Property revision = ResourceFactory.createProperty("http://usefulinc.com/ns/doap#revision");
		
		modelOut.add(mapping, genAt, dateLiteral)
			.add(mapping, wasGenBy, algorithm)
			.add(algorithm, name, "Generic Algorithm")
			.add(algorithm, genAt, dateLiteral)
			.add(algorithm, wasAssoc, versionedLDF)
			.add(versionedLDF, isVers, theLDF)
			.add(versionedLDF, release, version)
			.add(theLDF, name, "Generic Framework 1.0")
			.add(version, revision, "1.0");
		
		// iterate over statements
		Iterator<Statement> it = modelIn.listStatements();
		while(it.hasNext()) {
			Statement statement = it.next();
			Resource s = statement.getSubject();     
			Property p = statement.getPredicate(); 
			RDFNode o = statement.getObject();

			String md5 = MD5Utils.computeChecksum(s, p, o);
			
			Resource resource = ResourceFactory.createResource(preMd5 + md5);

			modelOut.add(resource, propS, s)
				.add(resource, propP, p)
				.add(resource, propO, o)
				.add(resource, propM, mapping);
			
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
		// TODO Auto-generated method stub
		return null;
	}

}
