package misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;

public class Test {

	public static final String NL = System.getProperty("line.separator");  

	public static void	printContent (URL url) throws IOException {

		BufferedReader	reader = null;
		try {
			URLConnection	conn = url.openConnection();
			//String	encoding = conn.getContentEncoding();
			//if (encoding == null || encoding.trim().length () == 0)
				//encoding = "UTF-8";
			
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));		
			StringBuffer	buf = new StringBuffer();
			String			next;
			//PrintWriter		writer = new PrintWriter(new FileOutputStream("c:/WebPredictCache/test.txt"));
			
			while ((next = reader.readLine()) != null) {
				buf.append(next);
				//writer.println(next);
				buf.append(NL);
			}
			//writer.flush();
			//writer.close();
			FileUtils.writeStringToFile(new File("c:/WebPredictCache/test.txt"), buf.toString(), "UTF-8");
			
			//System.out.println(buf.toString());
		}
		finally {
			if (reader != null)
				reader.close();
		}	
	}
	
	public static void main (String [] args) throws Exception {
		printContent(new URL("http://drakosha-ru.livejournal.com/183888.html"));
	}
}
