package de.linkinglod.db;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Hibernate object Link describing a single link which is contained in a mapping.
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 *
 */
@Entity
@Table(name="Link")
public class Link implements Serializable {

	public Link() {
	}

	private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "hashLink", unique = true, nullable = false, length = 132)
	private String hashLink;
	private long o1Id;
	private long o2Id;
	private long linkType;
	private double similarity;
	private String hashMapping;
	

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
