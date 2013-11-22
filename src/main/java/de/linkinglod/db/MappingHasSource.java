package de.linkinglod.db;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class MappingHasSource.
 * @author markus
 *
 */
@Entity
@Table(name="MappingHasSource")
public class MappingHasSource implements Serializable {

	public MappingHasSource(String hashMapping, int idSource, String type) {
		super();
		this.hashMapping = hashMapping;
		this.idSource = idSource;
		this.type = type;
	}

	private static final long serialVersionUID = 1L;
	private String hashMapping;
	private int idSource;
	private String type;
	
	@Id
	public String getHashMapping() {
		return hashMapping;
	}
	
	public void setHashMapping(String hashMapping) {
		this.hashMapping = hashMapping;
	}
	
	@Id
	public int getIdSource() {
		return idSource;
	}
	
	public void setIdSource(int idSource) {
		this.idSource = idSource;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	
}
