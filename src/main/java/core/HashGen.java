package wp.core;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

public class HashGen {

	public static void main (String [] args) throws Exception {
		String	filename = args [0];
		
		byte []	data = FileUtils.readFileToByteArray(new File(filename));
		
		System.out.println ("sha1:" + DigestUtils.shaHex(data));
	}
}
