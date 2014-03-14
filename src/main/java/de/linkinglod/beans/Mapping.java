package de.linkinglod.beans;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Mapping {
	
	private String uri, srcName, tgtName, storedAt;
	
	public Mapping(String uri, String srcName, String tgtName, String storedAt) {
		this.uri = uri;
		this.srcName = srcName;
		this.tgtName = tgtName;
		this.setStoredAt(storedAt);
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getSrcName() {
		return srcName;
	}
	public void setSrcName(String srcName) {
		this.srcName = srcName;
	}
	public String getTgtName() {
		return tgtName;
	}
	public void setTgtName(String tgtName) {
		this.tgtName = tgtName;
	}
	public String getStoredAt() {
		return storedAt;
	}
	public void setStoredAt(String storedAt) {
		this.storedAt = storedAt;
	}
}

