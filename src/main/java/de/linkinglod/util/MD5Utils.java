package de.linkinglod.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Markus Nentwig
 */
public class MD5Utils {
	
	private static MessageDigest md;
	
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// should never happen
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the MD5 message digest.
	 */
	public static void reset() {
		md.reset();
	}
	
	/**
	 * Build String which is used for generating the MD5 hash, if object is not a resource, surround it with ".
	 * @param s subject
	 * @param p predicate
	 * @param o object
	 * @return 
	 */
	public static String buildMd5String(Resource s, Property p, RDFNode o) {
		String statement = s.toString() + p.toString();
		// TODO should literal be modified with quotes? 
		// TODO Check if checksum is correct calculated!
		if (o instanceof Resource) {
			statement += o.toString();
		} else {
			statement+= "\"" + o.toString() + "\"";
		}
		statement += " .";

		return statement;
	}

	/**
	 * Checksum is calculated, string is converted to UTF8 previously.
	 * @param o 
	 * @param p 
	 * @param s 
	 * @param md
	 * @param text
	 * @return Hex representation of encoded MD5 hash
	 */
	public static String computeChecksum(Resource s, Property p, RDFNode o) {
		
		String text = buildMd5String(s, p, o);
		
		byte[] stringToUTF8byte = text.getBytes(Charset.forName("UTF8"));
		md.update(stringToUTF8byte);

		final byte[] resultDigest = md.digest();
		final String result = new String(Hex.encodeHex(resultDigest));
		return result;
	}
	
	/**
	 * Generates MD5 hash of the file content.
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String computeChecksum(String file) throws IOException {
		
		FileInputStream fis = new FileInputStream(new File(file));
		return DigestUtils.md5Hex(fis);
		
	}
	
}
