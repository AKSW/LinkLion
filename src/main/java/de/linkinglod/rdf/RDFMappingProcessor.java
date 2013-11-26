package de.linkinglod.rdf;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import de.linkinglod.io.MappingProcessor;
import de.linkinglod.service.LLProp;
import de.linkinglod.util.MD5Utils;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Markus Nentwig
 *
 */
public class RDFMappingProcessor implements MappingProcessor {
	
	public RDFMappingProcessor() {
	}

	@Override
	public Model transform(Model modelIn, String owner, Date timeStamp) {
		
		// prepare
		Model modelOut = ModelFactory.createDefaultModel();
		MD5Utils.reset();
		String ns = LLProp.getString("ns");
		String lim = LLProp.getString("delimiter");
		String vocProp = LLProp.getString("vocabularyProperty");
		String propString = ns + lim + vocProp + lim;
		String preMd5 = ns + lim + LLProp.getString("vocabularyLink") + LLProp.getString("fragmentIdentifier");
		Property propS = ResourceFactory.createProperty(propString + LLProp.getString("subjectAttribute"));
		Property propP = ResourceFactory.createProperty(propString + LLProp.getString("linkType"));
		Property propO = ResourceFactory.createProperty(propString + LLProp.getString("objectAttribute"));
		Property propM = ResourceFactory.createProperty(propString + LLProp.getString("hashMapping"));
		
		Resource mapping = ResourceFactory.createResource(preMd5 + "");

		
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
