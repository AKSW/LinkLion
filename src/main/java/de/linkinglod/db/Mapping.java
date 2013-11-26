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
    @Column(name = "hashMapping", unique = true, nullable = false, length = 32)
	private String hashMapping;
	private Timestamp timeGenerated;
	private long idOwner;
	private long idUploader;
	
	/**
	 * @param hashMapping
	 * @param timeGenerated
	 * @param idOwner
	 * @param idUploader
	 */
	public Mapping(String hashMapping, Timestamp timeGenerated, long idOwner,
			long idUploader) {
		super();
		this.hashMapping = hashMapping;
		this.timeGenerated = timeGenerated;
		this.idOwner = idOwner;
		this.idUploader = idUploader;
	}
	
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
	
	// interesting?
//	java.util.Date dt = new java.util.Date();
//
//	java.text.SimpleDateFormat sdf = 
//	     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//	String currentTime = sdf.format(dt);	
}
