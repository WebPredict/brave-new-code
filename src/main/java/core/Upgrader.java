package wp.core;

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
import java.util.StringTokenizer;

public class Upgrader {

	public static final String NL = System.getProperty("line.separator");  

	public Upgrader (String dir, final String extension, String afterLine, String insertLine) throws Exception {
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
				boolean	doAppend = false;
				while ((next = br.readLine()) != null) {
					if (doAppend) {
						doAppend = false;
						if (next.startsWith(insertLine)) {
							buf.append(next);
						}
						else {
							buf.append(insertLine);
							buf.append(NL);	
							buf.append(next);							
						}
					}
					else 
						buf.append(next);
					
					buf.append(NL);						
				
					if (next.startsWith(afterLine))
						doAppend = true;
				}
				
				if (doAppend) {
					buf.append(insertLine);
					buf.append(NL);
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
		
		if (args.length == 1) {
			BufferedReader	br = null;
			try {
				br = new BufferedReader(new FileReader(args [0]));
				String	next;
				while ((next = br.readLine()) != null) {
					StringTokenizer	tok = new StringTokenizer(next, ",");
					String	dir = tok.nextToken();
					String	ext = tok.nextToken();
					String	after = tok.nextToken();
					String	add = tok.nextToken();
					new Upgrader(dir, ext, after, add);
				}
				
				br.close();
			}
			finally {
				if (br != null)
					br.close();
			}
			return;
		}
		else if (args.length < 4) {
			System.out.println ("Usage: Upgrader <dir> <extension> <afterline> <insertline>");
			System.exit(1);
		}
		
		new Upgrader(args [0], args [1], args [2], args [3]);
		
	}
}
