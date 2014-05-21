package wp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RemoveLine {

	public RemoveLine (String dir, final String extension, String remove) throws Exception {
		File	root = new File(dir);
		
		String []	fileNames = root.list(new FilenameFilter () {

			public boolean accept(File arg0, String arg1) {
				return (arg1.endsWith(extension));
			}
			
		});
		
		for (String fileName : fileNames) {
			BufferedReader	br = null;
			PrintWriter		writer = null;
			
			try {
				StringBuffer	buf = new StringBuffer();
				String	fullName = dir + "/" + fileName;
				br = new BufferedReader(new InputStreamReader(new FileInputStream(fullName), "UTF-8"));
				
				String	next;
				while ((next = br.readLine()) != null) {									
					if (next.indexOf(remove) == -1) {
						buf.append(next);
						buf.append(WebUtils.NL);
					}
				}
				
				br.close();
				
				BufferedWriter	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullName), "UTF-8"));
				writer = new PrintWriter(bw);
				writer.append(buf.toString());
			}
			finally {
				if (br != null)
					br.close();
				if (writer != null)
					writer.close();
			}
		}
		
	}
	
	public static void main (String [] args) throws Exception {
		
		if (args.length < 3) {
			System.out.println ("Usage: ChangeLine <dir> <extension> <remove>");
			System.exit(1);
		}
		
		new RemoveLine(args [0], args [1], args [2]);
		
	}
}
