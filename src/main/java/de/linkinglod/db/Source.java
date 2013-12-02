package de.linkinglod.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class Source.
 * @author markus
 *
 */
@Entity
@Table(name="Source")
public class Source implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSource", unique = true, nullable = false)
	private long idSource;
	private String name;
	// TODO change if source gets more complex than uri
    @Column(name = "uri", unique = true, nullable = false, length = 100)
	private String uri;
	private String domain;
	private String lastVersion;
	
	public Source(long idSource, String name, String uri, String domain,
			String lastVersion) {
		super();
		this.idSource = idSource;
		this.name = name;
		this.uri = uri;
		this.domain = domain;
		this.lastVersion = lastVersion;
	}
	
	public Source() {
		
	}
	
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
