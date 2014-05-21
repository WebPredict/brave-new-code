package wp.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import wp.utils.Utils;
import wp.utils.WebUtils;

public class CachedURL {

	private String	urlStr;
	private String	cachedContent;
	private URL		url;
	private String	cookies;
	private String	cachedRobot;
	private ArrayList<String>	skipSites = new ArrayList<String>();
	
	public static final long	DEFAULT_CACHE_TIME = 600000l; // 10 minutes
	
	private long	cacheLifeMillis = DEFAULT_CACHE_TIME;
	
	public CachedURL (String urlStr) throws MalformedURLException {
		this(urlStr, DEFAULT_CACHE_TIME, null);
	}
	
	public CachedURL (String urlStr, String cookies) throws MalformedURLException {
		this(urlStr, DEFAULT_CACHE_TIME, cookies);
	}
	
	public CachedURL (URL url, long cacheLifeMillis, String cookies) {
		this.urlStr = url.toString();
		this.url = url;
		this.cacheLifeMillis = cacheLifeMillis;
		this.cookies = cookies;
	}
	
	public CachedURL (URL url, String cookies) {
		this(url, DEFAULT_CACHE_TIME, cookies);
	}
	
	public CachedURL (URL url, long cacheLifeMillis) {
		this(url, cacheLifeMillis, null);
	}
	
	public CachedURL (URL url) {
		this (url, DEFAULT_CACHE_TIME, null);
	}
	
	public void	setCacheLifeMillis (long time) {
		this.cacheLifeMillis = time;
	}
	
	public CachedURL (String urlStr, long cacheLifeMillis, String cookies) throws MalformedURLException {
		this.urlStr = urlStr;
		this.cacheLifeMillis = cacheLifeMillis;
		this.cookies = cookies;
		
		boolean	hasProtocol = urlStr.indexOf("://") != -1;
		
		if (!hasProtocol) 
			urlStr = "http://" + urlStr;
		
		this.url = new URL(urlStr);
	}
	
	// TODO: improve
	public String	translate (String url) {
		return (url.replaceAll("/", "_SL_").replaceAll(":", "_CO_").replaceAll("\\?", "_QU_").replaceAll("\\&", "_AN_"));
	}
	
	public InputStream	getContentStream () throws IOException {
		String	content = getContent();
		return (new ByteArrayInputStream(content.getBytes("UTF-8")));
	}
	
	public int	getContentSize () {
		return (cachedContent == null ? 0 : cachedContent.length());
	}
	
	//public boolean	isContentHtml () throws IOException {
		//String	content = getContent();
		//return (content != null && content.indexOf("<html>"))
	//}
	
	public boolean	crawlersForbidden () throws IOException {
		if (cachedRobot == null) {
			String	root = WebUtils.getRootDomain(urlStr);
			String	robotsUrlStr = root + "/robots.txt";
			
			String	fileUrl = robotsUrlStr;
			
			int		colonSlash = fileUrl.indexOf("://");
			if (colonSlash != -1)
				fileUrl = fileUrl.substring(colonSlash + 3);
	
			String	filename = Rater.getTheRater().getCacheDir() + translate(fileUrl);
			
			File	f = new File(filename);
			long	currentTime = System.currentTimeMillis();
			if (f.exists() && (currentTime - f.lastModified() < cacheLifeMillis)) {			
				cachedRobot = FileUtils.readFileToString(f, "UTF-8");
			}
			else { 
				URL		robotsUrl = new URL(robotsUrlStr);
				try {
					WebUtils.RawContent	rc = new WebUtils().getRawContent(robotsUrl, cookies);
					if (rc != null) { 
						cachedRobot = rc.rawContent; 			
						FileUtils.writeStringToFile(f, cachedRobot, rc.encoding);
					}
				}
				catch (IOException e) {
					cachedRobot = ""; // happens if there is no robots.txt on that site
				}
			}

			if (cachedRobot != null) {
				BufferedReader	br = new BufferedReader(new StringReader(cachedRobot));
				String	line;
				String	lastUserAgent = null;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("Disallow: ") && Utils.xequals(lastUserAgent, "*")) {
						int	slashIdx = line.indexOf("/");
						if (slashIdx != -1) 
							skipSites.add(root + line.substring(slashIdx));
					}
					else if (line.startsWith("User-agent: ") && line.length() > 12) 
						lastUserAgent = line.substring(12).trim();
				}
			}

		}

		if (skipSites.contains(urlStr))
			return (true);
		
		for (String site : skipSites) {
			if (urlStr.startsWith(site))
				return (true);
		}
		return (false);
	}
	
	public String	getContent () throws IOException {
		if (cachedContent == null) {
			String	fileUrl = urlStr;
			
			int		colonSlash = urlStr.indexOf("://");
			if (colonSlash != -1)
				fileUrl = urlStr.substring(colonSlash + 3);
			
			String	filename = Rater.getTheRater().getCacheDir() + translate(fileUrl);
			this.url = new URL(urlStr);
			
			if (filename.length() > 255) {
				// skip caching
				WebUtils.RawContent	rc = new WebUtils().getRawContent(url, cookies);
				cachedContent = rc.rawContent; 
			}
			else {
				File	f = new File(filename);
				long	currentTime = System.currentTimeMillis();
				if (f.exists() && (currentTime - f.lastModified() < cacheLifeMillis)) {			
					cachedContent = FileUtils.readFileToString(f, "UTF-8");
				}
				else { 
					WebUtils.RawContent	rc = new WebUtils().getRawContent(url, cookies);
					if (rc != null) { 
						cachedContent = rc.rawContent; 			
						FileUtils.writeStringToFile(f, cachedContent, rc.encoding);
					}
				}
			}
		}
		return (cachedContent);
	}
	
	public URL	getURL () {
		return (url);
	}
	
	public static void main (String [] args) throws Exception {
		CachedURL	url = new CachedURL("http://lifestyle.msn.com/relationships/articlerb.aspx?cp-documentid=17405318&gt1=32023");
		System.out.println(url.getContent());
		url = new CachedURL("www.nytimes.com");
		System.out.println(url.getContent());
	}
}
