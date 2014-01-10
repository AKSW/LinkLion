package de.linkinglod.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class LinkType.
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 *
 */
@Entity
@Table(name="LinkType")
public class LinkType implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLinkType", unique = true, nullable = false)
    private long idLinkType;
    @Column(name = "uri", unique = false, nullable = false, length = 100)
    private String uri;

	public LinkType() {
	}

	public long getIdLinkType() {
		return idLinkType;
	}
	
	public void setIdObject(long idLinkType) {
		this.idLinkType = idLinkType;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
}
