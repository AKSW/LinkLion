package de.linkinglod.db;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class MappingUsesMapping.
 * @author markus
 *
 */
@Entity
@Table(name="MappingUsesMapping")
public class MappingUsesMapping implements Serializable {

	public MappingUsesMapping(String mappingOne, String mappingTwo) {
		super();
		this.mappingOne = mappingOne;
		this.mappingTwo = mappingTwo;
	}

	private static final long serialVersionUID = 1L;
	private String mappingOne;
	private String mappingTwo;
	
	@Id
	public String getMappingOne() {
		return mappingOne;
	}
	
	public void setMappingOne(String mappingOne) {
		this.mappingOne = mappingOne;
	}
	
	@Id
	public String getMappingTwo() {
		return mappingTwo;
	}
	
	public void setMappingTwo(String mappingTwo) {
		this.mappingTwo = mappingTwo;
	}
	
	

}
