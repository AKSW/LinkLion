package de.linkinglod.beans;
/**
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 *
 */
public class RsDataset {
	
	private String uri, label, llUri;
	private Integer mCount, lCount;
	
	public RsDataset(String uri, String label, int mCount, String llUri) {
		this.setLabel(label);
		this.setLlUri(llUri);
		this.setmCount(mCount);
		this.setUri(uri);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLlUri() {
		return llUri;
	}

	public void setLlUri(String llUri) {
		this.llUri = llUri;
	}

	public Integer getmCount() {
		return mCount;
	}

	public void setmCount(Integer mCount) {
		this.mCount = mCount;
	}

	public Integer getlCount() {
		return lCount;
	}

	public void setlCount(Integer lCount) {
		this.lCount = lCount;
	}

}

