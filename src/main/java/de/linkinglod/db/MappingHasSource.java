package de.linkinglod.db;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class MappingHasSource.
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 *
 */
@Entity
@Table(name="MappingHasSource")
public class MappingHasSource implements Serializable {
	
	public MappingHasSource() {
	}

	private static final long serialVersionUID = 1L;
	private String hashMapping;
	private long idSource;
	private String type;
	
	@Id
	public String getHashMapping() {
		return hashMapping;
	}
	
	public void setHashMapping(String hashMapping) {
		this.hashMapping = hashMapping;
	}
	
	@Id
	public long getIdSource() {
		return idSource;
	}
	
	public void setIdSource(long idSource) {
		this.idSource = idSource;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	
}
