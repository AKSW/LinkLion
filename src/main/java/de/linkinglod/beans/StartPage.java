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
		frameworks.put("http://limes.sf.org", "LIMES &mdash; http://limes.sf.org");
		return frameworks;
		
	}
	
}
