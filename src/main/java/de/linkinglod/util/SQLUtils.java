package de.linkinglod.util;

/**
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 */
public class SQLUtils {

	public static String convertDateSeparators(String input) {
	    char[] chars = input.toCharArray();
	    chars[10] = ' ';
	    chars[13] = ':';
	    chars[16] = ':';
	    
	    return new String(chars);
	}
}
