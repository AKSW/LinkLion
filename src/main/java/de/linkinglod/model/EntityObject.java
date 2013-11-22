package de.linkinglod.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class EntityObject. Care for table name Object.
 * @author markus
 *
 */
@Entity
@Table(name="Object")
public class EntityObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idObject", unique = true, nullable = false)
    private long idObject;
    @Column(name = "uri", unique = false, nullable = false, length = 100)
    private String uri;
    @Column(name = "name", unique = false, nullable = true, length = 100)
    private String name;
	
	public EntityObject(long idObject, String uri, String name) {
		super();
		this.idObject = idObject;
		this.uri = uri;
		this.name = name;
	}

	public EntityObject() {
	}

	public long getIdObject() {
		return idObject;
	}
	
	public void setIdObject(long idObject) {
		this.idObject = idObject;
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
