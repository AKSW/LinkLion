package de.linkinglod.db;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate object Configuration.
 * @author markus
 *
 */
@Entity
@Table(name="Configuration")
public class Configuration implements Serializable {

	public Configuration(int idConfiguration, int framework, String hashMapping) {
		super();
		this.idConfiguration = idConfiguration;
		this.framework = framework;
		this.hashMapping = hashMapping;
	}

	private static final long serialVersionUID = 1L;
	private int idConfiguration;
	private int framework;
	private String hashMapping;
	
	@Id
	public int getIdConfiguration() {
		return idConfiguration;
	}
	
	public void setIdConfiguration(int idConfiguration) {
		this.idConfiguration = idConfiguration;
	}
	
	public int getFramework() {
		return framework;
	}
	
	public void setFramework(int framework) {
		this.framework = framework;
	}
	
	public String getHashMapping() {
		return hashMapping;
	}
	
	public void setHashMapping(String hashMapping) {
		this.hashMapping = hashMapping;
	}
	
}
