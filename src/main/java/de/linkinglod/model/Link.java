package de.linkinglod.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Describes a single link which is contained in a mapping.
 * @author markus
 *
 */
@Entity
@Table(name="Link")
public class Link implements Serializable {

	public Link(String hashLink, long o1Id, long o2Id, long linkType,
			double similarity, String hashMapping) {
		super();
		this.hashLink = hashLink;
		this.o1Id = o1Id;
		this.o2Id = o2Id;
		this.linkType = linkType;
		this.similarity = similarity;
		this.hashMapping = hashMapping;
	}

	private static final long serialVersionUID = 1L;
	private String hashLink;
	private long o1Id;
	private long o2Id;
	private long linkType;
	private double similarity;
	private String hashMapping;
	
    @Id
    public String getHashLink() { 
    	return hashLink; 
    }

    public void setHashLink(String hash) {
    	this.hashLink = hash; 
    }

	public long getO1Id() {
		return o1Id;
	}

	public void setO1Id(long o1Id) {
		this.o1Id = o1Id;
	}

	public long getO2Id() {
		return o2Id;
	}

	public void setO2Id(long o2Id) {
		this.o2Id = o2Id;
	}

	public long getLinkType() {
		return linkType;
	}

	public void setLinkType(long linkType) {
		this.linkType = linkType;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public String getHashMapping() {
		return hashMapping;
	}

	public void setHashMapping(String hashMapping) {
		this.hashMapping = hashMapping;
	}
	


}
