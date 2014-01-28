package de.linkinglod.util;

/**
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 */
public class SQLUtils {

	/**
	 * discard ^^xsd:date at the end, fix separators (not intelligent)
	 * @param input
	 * @return
	 */
	public static String convertDateSeparators(String input) {
		System.out.println("generatedAtTime:  " + input);
		input = input.substring(0, input.lastIndexOf("^") - 1);
		
	    char[] chars = input.toCharArray();
	    chars[10] = ' ';
	    chars[13] = ':';
	    chars[16] = ':';
		System.out.println("convertedTimeZone:" + chars.toString());

	    
	    return new String(chars);
	}

	/**
	 * format like this: 2013-11-29 10:34:44+01:00, omit +01:00
	 * TODO handle time zone
	 * @param input
	 * @return
	 */
	public static String toTimestamp(String input) {
		if (input.contains("+")) { 
			return input.substring(0, input.lastIndexOf("+"));	
		}
		else {
			return input;
		}
	}
}
