package de.linkinglod.db;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Hibernate object Framework describing the link discovery framework.
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 *
 */
@Entity
@Table(name="Framework")
public class Framework implements Serializable {

	private static final long serialVersionUID = 1L;
	private int idFramework;
	private String url;
	private String name;
	private String lastVersion;
	private long idOwner;
	
	public Framework() {
	}
	
	@Id
	public int getIdFramework() {
		return idFramework;
	}
	
	public void setIdFramework(int idFramework) {
		this.idFramework = idFramework;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLastVersion() {
		return lastVersion;
	}
	
	public void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
	}
	
	public long getIdOwner() {
		return idOwner;
	}
	
	public void setIdOwner(long idOwner) {
		this.idOwner = idOwner;
	}

}
