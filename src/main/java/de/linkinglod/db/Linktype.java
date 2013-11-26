package de.linkinglod.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class Linktype.
 * @author markus
 *
 */
@Entity
@Table(name="Linktype")
public class Linktype implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLinktype", unique = true, nullable = false)
    private long idLinktype;
    @Column(name = "uri", unique = false, nullable = false, length = 100)
    private String uri;
	
	public Linktype(long idLinktype, String uri) {
		super();
		this.idLinktype = idLinktype;
		this.uri = uri;
	}

	public Linktype() {
	}

	public long getIdLinktype() {
		return idLinktype;
	}
	
	public void setIdObject(long idLinktype) {
		this.idLinktype = idLinktype;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

}
