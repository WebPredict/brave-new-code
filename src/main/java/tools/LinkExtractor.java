package wp.tools;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

import wp.core.CachedURL;
import wp.core.WebUtils;
import wp.model.LinkInfo;

/**
 * Extracts all links from a URL and saves to a file.
 * @author Jeff
 *
 */
public class LinkExtractor {

	private String	outputFile;
	private HashSet<String>	seenLinks = new HashSet<String>();
	private int	cutoff = 10000;
	
	public static void main (String [] args) throws Exception {
		String urlFilename = args [0];
		String	outputFilename = args [1];
		Integer	levels = Integer.parseInt(args [2]);
		Integer	cutoff = Integer.parseInt(args [3]);
		
		new LinkExtractor(outputFilename).extract(urlFilename, levels, cutoff);
	}
	
	public LinkExtractor (String outputFile) {
		this.outputFile = outputFile;
	}
		
	public void	extract (String startUrl, int levels, int cutoff) throws Exception {
		this.cutoff = cutoff;
		WebUtils	wu = new WebUtils();
		System.out.println ("Extracting links on: " + startUrl);

		List<LinkInfo>	links = wu.extractLinks (new CachedURL(startUrl), true, true);

		for (LinkInfo info : links)
			seenLinks.add(info.getLink());
		
		if (levels > 1 && links.size() < cutoff) {
			for (LinkInfo s : links) {
				try {
					extractRec(s.getLink(), levels - 1);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		PrintWriter	pw = null;
		
		try {
			pw = new PrintWriter(new FileWriter(outputFile));
			for (String link : seenLinks) {
				System.out.println(link);
				pw.println(link);
			}
		}
		finally {
			if (pw != null)
				pw.close();
		}
	}
	
	public void	extractRec (String startUrl, int levels) throws Exception {
		if (seenLinks.size() >= cutoff)
			return;
		
		System.out.println ("Extracting links on: " + startUrl);
		
		WebUtils	wu = new WebUtils();
		List<LinkInfo>	links = wu.extractLinks (new CachedURL(startUrl), true, true);
		
		for (LinkInfo info : links)
			seenLinks.add(info.getLink());
		
		if (levels > 1) {
			for (LinkInfo s : links) {
				extractRec(s.getLink(), levels - 1);
			}
		}
	}
}
