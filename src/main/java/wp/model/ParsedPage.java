package wp.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import wp.utils.Utils;

@Entity
public class ParsedPage {

	private String	rawContent;
	private URL	url;	
	private String	urlStr;
	private int	contentSize;
	private int	strippedContentSize;
	private String title;
	private String	description;
	private String  keywords;
	private HashSet<String>	keywordSet = new HashSet<String>();
	private HashSet<String>	headlinesSet = new HashSet<String>();
	private HashSet<String>	linksSet = new HashSet<String>();
	private StringBuffer	contentBuffer = new StringBuffer();
	
	private String  headlines;
	private boolean hasAds;
	private boolean hasPopups;
	private double linkToContentRatio;
	private double imageToContentRatio;
	private int	numLinks;
	private int numImages;
	private String	firstLine;
	private Date	timestamp = new Date();
	private Long	id;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length=1024)
	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
		try {
			this.url = new URL(urlStr);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("PROBLEM WITH URL: " + urlStr);
			//e.printStackTrace();
		}
	}
	
	@Transient
	public String	getFormattedLinkToContentRatio () {
		StringBuffer	buf = new StringBuffer("#.#####");
		
		DecimalFormat	formatter = new DecimalFormat(buf.toString());
		return (formatter.format(linkToContentRatio));
	}

	@Transient
	public String	getFormattedImageToContentRatio () {
		StringBuffer	buf = new StringBuffer("#.#####");
		
		DecimalFormat	formatter = new DecimalFormat(buf.toString());
		return (formatter.format(imageToContentRatio));
	}

	@Transient
	public StringBuffer	getContentBuffer () {
		return (contentBuffer);
	}
	
	@Column
	public int getNumLinks() {
		return numLinks;
	}

	public void setNumLinks(int numLinks) {
		this.numLinks = numLinks;
	}

	@Column(length=1024)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=1024)
	public String getHeadlines() {
		return headlines;
	}

	public void setHeadlines(String headlines) {
		this.headlines = headlines;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Column
	public Date getTimestamp() {
		return timestamp;
	}

	public ParsedPage () {
	
	}
	
	public ParsedPage (String rawContent, URL url, int contentSize, int strippedContentSize, String title, String desc, 
			String [] keywords, boolean hasAds, boolean hasPopups, HashSet<String> linksSet, int numImages, String firstLine, 
			String [] headlines) 
	{
		init(rawContent, url, contentSize, strippedContentSize, title, desc, keywords, hasAds, 
				hasPopups, linksSet, numImages, firstLine, headlines);
	}
	
	public void init (String rawContent, URL url, int contentSize, int strippedContentSize, String title, String desc, 
			String [] keywords, boolean hasAds, boolean hasPopups, HashSet<String> linksSet, int numImages, String firstLine, 
			String [] headlines) 
	{
		setRawContent(StringEscapeUtils.unescapeHtml(rawContent));
		setUrl(url);
		this.contentSize = contentSize;
		this.title = StringEscapeUtils.unescapeHtml(title);
		this.description = StringEscapeUtils.unescapeHtml(desc);
		setKeywords(StringEscapeUtils.unescapeHtml(StringUtils.join(keywords, ",")));
		this.hasAds = hasAds;
		this.hasPopups = hasPopups;
		this.linksSet = linksSet;
		this.numLinks = linksSet == null ? 0 : linksSet.size();
		this.numImages = numImages;
		if (contentSize != 0) {
			this.linkToContentRatio = (double)numLinks / (double)contentSize;
			this.imageToContentRatio = (double)numImages / (double)contentSize;
		}
		this.firstLine = StringEscapeUtils.unescapeHtml(firstLine);
		this.headlines = StringEscapeUtils.unescapeHtml(StringUtils.join(headlines, " "));
		this.strippedContentSize = strippedContentSize;
	}

	public ParsedPage (String plainText) {
		setRawContent(plainText);
		strippedContentSize = contentSize = rawContent == null ? 0 : rawContent.length();
		firstLine = strippedContentSize < 200 ? rawContent : rawContent.substring(0, 200);
	}

	public void	doneParsing () {
		setRawContent(StringEscapeUtils.unescapeHtml(contentBuffer.toString()));
		setStrippedContentSize(rawContent.length());
		if (contentSize == 0)
			contentSize = strippedContentSize;
		
		this.numLinks = linksSet == null ? 0 : linksSet.size();
		
		String []	headlinesArr = new String [headlinesSet.size()];
		headlinesSet.toArray(headlinesArr);
		this.headlines = StringEscapeUtils.unescapeHtml(StringUtils.join(headlinesArr, " "));
		// TODO: finish
		if (contentSize != 0) {
			this.linkToContentRatio = (double)numLinks / (double)contentSize;
			this.imageToContentRatio = (double)numImages / (double)contentSize;	
		}
		
		if (StringUtils.isEmpty(firstLine))
			firstLine = strippedContentSize < 200 ? rawContent : rawContent.substring(0, 200);
	}
	
	@Transient
	public HashSet<String>	getKeywordSet() {
		return (keywordSet);
	}
	
	@Transient
	public HashSet<String>	getLinksSet() {
		return (linksSet);
	}
	
	@Transient
	public HashSet<String>	getHeadlinesSet() {
		return (headlinesSet);
	}
	
	@Column
	public String getFirstLine() {
		return firstLine;
	}

	public int	snippetSize () {
		return (firstLine == null ? 0 : firstLine.length());
	}
	
	public void	addSnippet (String s) {
		if (firstLine == null)
			firstLine = s;
		else
			firstLine += s;
	}
	
	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}

	@Column
	public boolean isHasAds() {
		return hasAds;
	}

	public void setHasAds(boolean hasAds) {
		this.hasAds = hasAds;
	}

	@Column
	public boolean isHasPopups() {
		return hasPopups;
	}

	public void setHasPopups(boolean hasPopups) {
		this.hasPopups = hasPopups;
	}

	@Column
	public double getLinkToContentRatio() {
		return linkToContentRatio;
	}

	@Transient
	public double getImageToContentRatio() {
		return imageToContentRatio;
	}
	
	public void setLinkToContentRatio(double linkToContentRatio) {
		this.linkToContentRatio = linkToContentRatio;
	}

	@Column
	public int getStrippedContentSize() {
		return strippedContentSize;
	}

	public void setStrippedContentSize(int strippedContentSize) {
		this.strippedContentSize = strippedContentSize;
	}

	@Column
	public int getNumImages() {
		return numImages;
	}

	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	@Column
	public int getContentSize() {
		return contentSize;
	}

	public void setContentSize(int contentSize) {
		this.contentSize = contentSize;
	}

	@Column
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(length=1024)
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		keywordSet.clear();
		if (keywords != null) 
		{
			StringTokenizer	tok = new StringTokenizer(keywords, ",");
			while (tok.hasMoreTokens())
				keywordSet.add(tok.nextToken().trim());
		}
		this.keywords = keywords;
	}

	@Transient
	public int	getContentLength () {
		return (rawContent == null ? 0 : rawContent.length());
	}
	
	@Transient
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
		this.urlStr = url == null ? null : url.toString();
	}

	@Column(length=100000)
	public String getRawContent() {
		return rawContent;
	}
	
	public void setRawContent(String rawContent) {
		this.rawContent = Utils.limit(rawContent, 100000);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("URL:"  + urlStr); 
		sb.append("\n");
		sb.append("contentSize:" + String.valueOf(contentSize));
		sb.append("\n");
		sb.append("strippedContentSize:" + String.valueOf(strippedContentSize));
		sb.append("\n");
		sb.append("title:"  + title); 
		sb.append("\n");
		sb.append("description:"  + description); 
		sb.append("\n");
		sb.append("keywords:"  + keywords); 
		sb.append("\n");
		sb.append("keywordSet.size:" + keywordSet.size());
		sb.append("\n");
		
		sb.append("\n== keywordSet ==\n");
		Iterator<String> keywordss = keywordSet.iterator();

		while (keywordss.hasNext())
			sb.append(keywordss.next()+"\n");
		
		
		sb.append("headlinesSet.size:" + headlinesSet.size());
		sb.append("\n");

		sb.append("\n== headlinesSet ==\n");
		Iterator<String> headlines = headlinesSet.iterator();

		while (headlines.hasNext())
			sb.append(headlines.next()+"\n");
				
		sb.append("linksSet.size:" + linksSet.size());
		sb.append("\n== links ==\n");
		Iterator<String> links = linksSet.iterator();

		while (links.hasNext())
			sb.append(links.next()+"\n");
		
		sb.append("\nhasAds:");
		sb.append(String.valueOf(hasAds));
		
		sb.append("\nhasPopups:");
		sb.append(String.valueOf(hasPopups));
		
		sb.append("\nlinkToContentRatio:");
		sb.append(String.valueOf(linkToContentRatio));
		sb.append("\n");
		
		return sb.toString();
	}
}
