package de.linkinglod.beans;

import java.util.HashMap;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class StartPage {
	
	private HashMap<String, String> frameworks;

	public HashMap<String, String> getFrameworks() {
		
		frameworks = new HashMap<>();
		frameworks.put("http://www.linklion.org/version/HASHCODE", "LIMES v0.8 &mdash; http://limes.sf.org");
		return frameworks;
		
	}
	
}
