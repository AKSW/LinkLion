package de.linkinglod.db;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class mapping.
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 * 
 */
@Entity
@Table(name="Mapping")
public class Mapping implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name = "hashMapping", unique = true, nullable = false, length = 132)
	private String hashMapping;
    @Column(name = "idFramework", nullable = false, length = 11)
	private long idFramework;
    private String semanticType;
	private Timestamp timeGenerated;
	private long idOwner;
	private long idUploader;
	private long idLinkType;
	
	
	/**
	 *  Create a Mapping hibernate object.
	 */
	public Mapping() {
	}
	
	public String getHashMapping() {
		return hashMapping;
	}
	
	public void setHashMapping(String hashMapping) {
		this.hashMapping = hashMapping;
	}
	
	public Timestamp getTimeGenerated() {
		return timeGenerated;
	}
	
	public void setTimeGenerated(Timestamp timeGenerated) {
		this.timeGenerated = timeGenerated;
	}
	
	public long getIdOwner() {
		return idOwner;
	}
	
	public void setIdOwner(long idOwner) {
		this.idOwner = idOwner;
	}
	
	public long getIdUploader() {
		return idUploader;
	}
	public void setIdUploader(long idUploader) {
		this.idUploader = idUploader;
	}

	public long getIdLinkType() {
		return idLinkType;
	}

	public void setIdLinkType(long idLinkType) {
		this.idLinkType = idLinkType;
	}

	public String getSemanticType() {
		return semanticType;
	}

	public void setSemanticType(String semanticType) {
		this.semanticType = semanticType;
	}
}
