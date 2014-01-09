package de.linkinglod.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class RDFSResource.
 * @author markus
 *
 */
@Entity
@Table(name="RDFSResource")
public class RDFSResource implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idResource", unique = true, nullable = false)
    private long idResource;
    @Column(name = "uri", unique = false, nullable = false, length = 512)
    private String uri;
    @Column(name = "name", unique = false, nullable = true, length = 512)
    private String name;
	
	public RDFSResource(long idResource, String uri, String name) {
		super();
		this.idResource = idResource;
		this.uri = uri;
		this.name = name;
	}
	
	public RDFSResource() {
	}

	public long getIdResource() {
		return idResource;
	}
	
	public void setIdResource(long idResource) {
		this.idResource = idResource;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
