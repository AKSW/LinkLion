package de.linkinglod.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate class User.
 * @author markus
 *
 */
@Entity
@Table(name="User")
public class User implements Serializable {

//	public User(long idUser, String name) {
//		super();
//		this.idUser = idUser;
//		this.name = name;
//	}

	public User() {
	}

	private static final long serialVersionUID = 1L;
	private long idUser;
	private String name;

	@Id
	public long getIdUser() {
		return idUser;
	}

	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
