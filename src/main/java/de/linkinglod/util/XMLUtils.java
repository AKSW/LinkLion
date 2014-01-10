package de.linkinglod.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 */
public class XMLUtils {

	/**
	 * Converts java Date class into XSDdateTime format.
	 * @param timeStamp
	 * @return
	 */
	public static String toXSD(Date timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(timeStamp);
	}
}
