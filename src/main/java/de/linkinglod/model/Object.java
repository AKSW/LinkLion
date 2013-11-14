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
@Table(name="Object")
public class Object implements Serializable {

	public Object(long idObject, String uri, String name) {
		super();
		this.idObject = idObject;
		this.uri = uri;
		this.name = name;
	}

	private static final long serialVersionUID = 1L;
	
    private long idObject;
    private String uri;
    private String name;
    
    @Id
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
