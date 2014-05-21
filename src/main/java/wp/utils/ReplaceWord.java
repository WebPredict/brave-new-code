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

public class ReplaceWord {

	public ReplaceWord (String dir, final String extension, String from, String to) throws Exception {
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
					if (next.indexOf(from) != -1)
						buf.append(next.replaceAll(from, to));
					else
						buf.append(next);
					buf.append(WebUtils.NL);											
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
		
		if (args.length < 4) {
			System.out.println ("Usage: ReplaceWord <dir> <extension> <from> <to>");
			System.exit(1);
		}
		
		new ReplaceWord(args [0], args [1], args [2], args [3]);
		
	}
}