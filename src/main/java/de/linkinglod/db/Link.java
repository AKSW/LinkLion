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
    @Column(name = "hashLink", unique = true, nullable = false, length = 128)
	private String hashLink;
	private long res1Id;
	private long res2Id;
	private long linkType;
	private double similarity;
	private String hashMapping;
	

    public String getHashLink() { 
    	return hashLink; 
    }

    public void setHashLink(String hash) {
    	this.hashLink = hash; 
    }

	public long getRes1Id() {
		return res1Id;
	}

	public void setRes1Id(long res1Id) {
		this.res1Id = res1Id;
	}

	public long getRes2Id() {
		return res2Id;
	}

	public void setRes2Id(long res2Id) {
		this.res2Id = res2Id;
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
