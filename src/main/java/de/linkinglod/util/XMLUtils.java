package de.linkinglod.util;

import java.text.SimpleDateFormat;
import java.util.Date;

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
