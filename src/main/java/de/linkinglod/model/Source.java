package de.linkinglod.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author markus
 *
 */
@Entity
@Table(name="Source")
public class Source implements Serializable {

	public Source(long idSource, String name, String uri, String domain,
			String lastVersion) {
		super();
		this.idSource = idSource;
		this.name = name;
		this.uri = uri;
		this.domain = domain;
		this.lastVersion = lastVersion;
	}

	private static final long serialVersionUID = 1L;
	private long idSource;
	private String name;
	private String uri;
	private String domain;
	private String lastVersion;
	
	@Id
	public long getIdSource() {
		return idSource;
	}
	
	public void setIdSource(long idSource) {
		this.idSource = idSource;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getLastVersion() {
		return lastVersion;
	}
	
	public void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
	}

}
