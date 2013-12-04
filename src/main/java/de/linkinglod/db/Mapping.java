package de.linkinglod.db;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class mapping.
 * @author markus
 * 
 */
@Entity
@Table(name="Mapping")
public class Mapping implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name = "hashMapping", unique = true, nullable = false, length = 132)
	private String hashMapping;
	private Timestamp timeGenerated;
	private long idOwner;
	private long idUploader;
	
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
}
