package wp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import wp.model.LinkInfo;
import wp.model.ParsedPage;

public class WebUtils {

	public static final String NL = System.getProperty("line.separator");

	public static HashSet<String> AD_LIST;

	public static String stripTags(String content) {
		throw new RuntimeException("TODO");
	}

	private String curLineLower;
	private String curLine;
	private boolean genericizeNumbers;
	private BufferedReader reader;
	public static final boolean DEBUG = false;
	public static final boolean OLD_PARSER = true;
	public static final String MONEY_EXPR = "\\$[0-9]+(\\.[0-9]+)?";
	public static final String MONEY_TOKEN = "WP_MONEY_TOKEN";
	public static final String NUM_EXPR = "[0-9]+(\\.[0-9]+)?";
	public static final String NUM_TOKEN = "WP_NUM_TOKEN";
	public static final String TIME_EXPR = "([0-9]?[0-9])(:[0-9][0-9])?( ?[a,p,A,P][m,M])";
	public static final String TIME_TOKEN = "WP_TIME_TOKEN";

	public static void initAdList() {
		if (AD_LIST != null)
			return;

		AD_LIST = new HashSet<String>();

		BufferedReader br = null;

		try {
			if (!new File(Rater.getTheRater().getCommonDir() + "adlist.txt")
					.exists())
				return;

			br = new BufferedReader(new FileReader(Rater.getTheRater()
					.getCommonDir()
					+ "adlist.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("*"))
					AD_LIST.add(line.substring(1));
				else
					AD_LIST.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * 
	 * Remove some punctuation.
	 * 
	 * @param word
	 * @param normalize
	 * @return
	 */
	public static String cleanWord(String word, boolean normalize) {
		String newWord = word;
		int startIdx = 0;
		int endIdx = word.length();

		switch (word.charAt(0)) {
		case '.':
		case ',':
		case ';':
		case ':':
		case '?':
		case '!':
		case '\"':
		case '\'':
		case '(':
		case ')':
		case '[':
		case ']':
			startIdx = 1;
		}

		switch (word.charAt(word.length() - 1)) {
		case '.':
		case ',':
		case ';':
		case ':':
		case '?':
		case '!':
		case '\"':
		case '\'':
		case '(':
		case ')':
		case '[':
		case ']':
			endIdx--;
		}

		if (startIdx >= endIdx)
			return (null);

		newWord = word.substring(startIdx, endIdx);

		if (normalize)
			return (newWord.toLowerCase());
		return (newWord);
	}

	public ParsedPage newParseOther(CachedURL url, boolean genericizeNumbers,
			boolean careAboutAds, int snippetSize, ParsedPage pp) throws IOException {

		String content = url.getContent();
		if (pp == null)
			pp = new ParsedPage(content);

		return (pp);
	}

	private String lineBreak(String content) {
		if (content == null)
			return (null);

		StringBuffer buf = new StringBuffer();

		boolean lookingForEndTag = false;
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);

			buf.append(c);

			if (c == '>' && lookingForEndTag) {
				if (content.indexOf(NL, i + 1) != i + 1)
					buf.append(NL); // don't duplicate line breaks
				lookingForEndTag = false;
			} else if (c == '<' && i < content.length() - 1
					&& content.charAt(i + 1) == '/')
				lookingForEndTag = true;
		}

		return (buf.toString());
	}

	public static String	getRootDomain (String link) {
		if (StringUtils.isEmpty(link))
			return (link);
		link = link.trim();
		int	rightSlashIdx = -1;
		if (link.startsWith("http://") && link.length() > 7) 
			rightSlashIdx = link.indexOf("/", 7);
		else
			rightSlashIdx = link.indexOf("/");
		
		if (rightSlashIdx == -1)
			return (link);
		return (link.substring(0, rightSlashIdx));
	}
	
	public List<LinkInfo> extractLinks(CachedURL url, 
			boolean onlyOtherSites, boolean onlyThisDomain) throws Exception {

		String urlStr = url.getURL().toString();
		String	root = getRootDomain(urlStr);
		
		InputStream is = url.getContentStream();
		List<LinkInfo> linkList = new ArrayList<LinkInfo>();
		try {
			Tidy tidy = new Tidy();

			tidy.setShowWarnings(false);
			
			Document doc = tidy.parseDOM(is, null);
			NodeList list = doc.getChildNodes();

			int numNodes = list.getLength();
			for (int i = 0; i < numNodes; i++) {
				Node node = list.item(i);
				followNodeForLinks(node, "");
			}

			list = doc.getElementsByTagName("a");
			numNodes = list.getLength();
			for (int n = 0; n < numNodes; n++) {
				Element e = (Element) list.item(n);
				String hrefVal = e.getAttribute("href");
				if (StringUtils.isBlank(hrefVal))
					continue;

				if (!hrefVal.startsWith("http://")) {
					if (hrefVal.startsWith("#") || hrefVal.startsWith("javascript") || hrefVal.equals("/"))
						continue; // no point in doing anchors on same page
					if (hrefVal.startsWith("/"))
						hrefVal = root + hrefVal;
					else
						hrefVal = root + "/" + hrefVal;
				}

				if (onlyOtherSites && hrefVal.startsWith(root))
					continue;

				if (onlyThisDomain && !hrefVal.startsWith(root))
					continue;
				
				if (isUnsupportedFormat(hrefVal))
					continue;
				
				String	linkContext = null;
				Node	parent = e.getParentNode();
				if (parent != null) {
					NodeList	children = parent.getChildNodes();
					if (children != null) {
						int	numChildren = children.getLength();
						for (int i = 0; i < numChildren; i++) {
							Node	cur = children.item(i);
							if (cur == e) {							
								if (i > 0) {
									Node	prev = children.item(i - 1);
									linkContext = getTextOrChildTextIfAvailable(prev);
								}
								
								if (i < numChildren - 1) {
									Node	next = children.item(i + 1);
									if (linkContext == null)
										linkContext = getTextOrChildTextIfAvailable(next);
									else
										linkContext += " " + getTextOrChildTextIfAvailable(next);
								}
								
								break;
							}
						}
					}
				}
				
				String	linkText = isChildTextNode(e) ? getChildText(e, false) : hrefVal;
				linkList.add(new LinkInfo(hrefVal, linkText, linkContext));
			}
		} finally {
			if (is != null)
				is.close();
		}
		return (linkList);
	}
 
	public static ArrayList<javax.servlet.http.Cookie>	getCookiesFor (HttpServletRequest request, String urlStr) {
		javax.servlet.http.Cookie[]	cookies = request.getCookies();
		
		ArrayList<javax.servlet.http.Cookie>	cookiesToSend = new ArrayList<javax.servlet.http.Cookie>();
		
		if (cookies != null) {
			for (javax.servlet.http.Cookie cookie : cookies) {
				String	domain = cookie.getDomain();
				if (StringUtils.isEmpty(domain))
					continue;
				
				if (urlStr.indexOf(domain) != -1)
					cookiesToSend.add(cookie);
			}
		}
		return (cookiesToSend);
	}

	public static boolean	isUnsupportedFormat (String url) {
	
		if (StringUtils.isEmpty(url))
			return (true);
		
		url = url.toLowerCase();
		// TODO: complete this list
		if (url.indexOf("youtube.com") != -1 || url.endsWith("mp3") || url.endsWith("gif") || url.endsWith("jpg") ||
				url.endsWith("jpeg") || url.endsWith("pdf") || url.endsWith("gz") || url.endsWith("zip") || 
				url.endsWith("wmv") || url.endsWith("swf") || url.endsWith("avi"))
			return (true);
		
		return (false);
	}
	
	public ParsedPage newParse(CachedURL url, boolean genericizeNumbers,
			boolean careAboutAds, boolean useStrictParser, int snippetSize, ParsedPage pp)
			throws IOException {

		if (!useStrictParser) {
			String content = url.getContent();
			content = lineBreak(content);
			if (DEBUG)
				System.out.println(content);

			reader = new BufferedReader(new StringReader(content));
			try {
				return (this.getContentInt(url, genericizeNumbers,
						careAboutAds, pp));
			} finally {
				if (reader != null)
					reader.close();
			}
		}

		InputStream is = url.getContentStream();

		try {
			Tidy tidy = new Tidy();
			tidy.setShowWarnings(false);
			// TODO: find out what to convert this to
			//tidy.setCharEncoding(Configuration.UTF8);
			
			Document doc = tidy.parseDOM(is, null);
			NodeList list = doc.getChildNodes();

			// TODO: if doc/list is null/empty, check if it's raw text:
			if (list == null || list.getLength() == 0)
				return (newParseOther(url, genericizeNumbers, careAboutAds, snippetSize, pp));

			if (pp == null)
				pp = new ParsedPage();
			pp.setUrl(url.getURL());
			int numNodes = list.getLength();
			for (int i = 0; i < numNodes; i++) {
				Node node = list.item(i);
				followNode(node, pp, genericizeNumbers, careAboutAds, snippetSize, "");
			}

			list = doc.getElementsByTagName("meta");
			numNodes = list.getLength();
			for (int n = 0; n < numNodes; n++) {
				Element e = (Element) list.item(n);
				String name = e.getAttribute("name");
				if (!StringUtils.isBlank(name)) {
					name = name.toLowerCase();
					if (name.equals("keywords")) {
						pp.setKeywords(unescapeText(e.getAttribute("content")));
					} else if (name.equals("description")) {
						pp.setDescription(unescapeText(e.getAttribute("content")));
					}
				}
			}

			pp.setContentSize(url.getContentSize());
			pp.doneParsing();
			return (pp);
		} finally {
			if (is != null)
				is.close();
		}
	}

	public String getTextOrChildTextIfAvailable (Node node) {
		if (node instanceof Text) 
			return (node.getNodeValue().trim());
		else {
			NodeList	list = node.getChildNodes();
			if (list != null && list.getLength() > 0 && (list.item(0) instanceof Text))
				return (list.item(0).getNodeValue().trim());
		}
		return (null);
	}
	
	public boolean isChildTextNode(Node node) {
		NodeList list = node.getChildNodes();
		if (list == null || list.getLength() == 0)
			return (false);

		// return (list.item(0).getNodeName().equals("#text"));
		return (list.item(0) instanceof Text);
	}

	public String unescapeText(String text) {
		return (text == null ? null : StringEscapeUtils.unescapeHtml(text));
		//return (text);
	}

	public String getChildText(Node node, boolean genericizeNumbers) {
		String text = node.getChildNodes().item(0).getNodeValue();
		//text = StringEscapeUtils.unescapeHtml(text);
		//text.replaceAll("&nbsp;", " ");
		text = text.trim();
//		if (genericizeNumbers)
//			text = text.replaceAll(TIME_EXPR, TIME_TOKEN).replaceAll(
//					MONEY_EXPR, MONEY_TOKEN).replaceAll(NUM_EXPR, NUM_TOKEN);
		return (text);
	}

	public boolean hasAds(String snippet) {
		initAdList();
		String lower = snippet.toLowerCase();
		for (String s : AD_LIST)
			if (lower.indexOf(s) != -1)
				return (true);

		return (false);
	}

	public void followNodeForLinks(Node node, String indent) {
		NodeList list = node.getChildNodes();
		String	nodeName = node.getNodeName();
		if (StringUtils.isEmpty(nodeName))
			return;
		
		nodeName = nodeName.toLowerCase();
		String nodeValue = node.getNodeValue();

		if (DEBUG)
			System.out.println(indent + "NAME: " + nodeName + "VALUE: " + nodeValue);
		boolean isTextNode = isChildTextNode(node);

		/**
		 * if current node is one of interest (e.g., "a"), and child node is
		 * #text, then add it to parsed page. otherwise, follow it deeper (if
		 * it's not #text).
		 */
		if (isTextNode && nodeName.equals("a")) {
			// String link = getChildText(node, false);
			// if (!links.contains(link))
			// links.add(link);
		} else {
			int numNodes = list == null ? 0 : list.getLength();
			for (int i = 0; i < numNodes; i++) {
				Node childNode = list.item(i);
				followNodeForLinks(childNode, indent + "  ");
			}
		}
	}

	public void followNode(Node node, ParsedPage pp, boolean genericizeNumbers,
			boolean careAboutAds, int snippetSize, String indent) {
		NodeList list = node.getChildNodes();
		String	nodeName = node.getNodeName();
		if (StringUtils.isEmpty(nodeName))
			return;
		
		nodeName = nodeName.toLowerCase();
		String nodeValue = node.getNodeValue();

		if (DEBUG)
			System.out.println(indent + "NAME: " + nodeName + "VALUE: "
					+ nodeValue);
		boolean isTextNode = isChildTextNode(node);

		/**
		 * if current node is one of interest (e.g., "a"), and child node is
		 * #text, then add it to parsed page. otherwise, follow it deeper (if
		 * it's not #text).
		 */
		if (isTextNode) {
			if (nodeName.equals("a")) {
				pp.getLinksSet().add(getChildText(node, genericizeNumbers));
			} else if (nodeName.equals("title")) {
				pp.setTitle(getChildText(node, genericizeNumbers));
			} else if (nodeName.equals("h1") || nodeName.equals("h2")
					|| nodeName.equals("h3") || nodeName.equals("h4")) {
				pp.getHeadlinesSet().add(getChildText(node, genericizeNumbers));
			} else if (nodeName.equals("script")) {
				String scriptText = getChildText(node, genericizeNumbers);
				if (StringUtils.isNotEmpty(scriptText)) {
					if (careAboutAds && !pp.isHasAds()) {
						boolean hasAds = hasAds(scriptText);
						if (hasAds)
							pp.setHasAds(hasAds);
					}
					if (!pp.isHasPopups()) {
						scriptText = scriptText.toLowerCase();
						boolean foundPopups = scriptText.indexOf("window.open") != -1
								|| scriptText.indexOf("javascript:window.open") != -1;
						if (foundPopups)
							pp.setHasPopups(foundPopups);
					}
				}
			} else if (!nodeName.equals("style")) {
				String childText = getChildText(node, genericizeNumbers);
				if (childText != null && childText.length() > 50) {
					int	curSnipLen = pp.snippetSize();
					
					if (curSnipLen < snippetSize - 4) {
						int	diff = (snippetSize - 4) - curSnipLen;
						String	txtToUse;
						if (childText.length () < diff)
							txtToUse = " " + childText;
						else
							txtToUse = " " + childText.substring(0, diff) + "...";
						pp.addSnippet(txtToUse);
					}
				}
				pp.getContentBuffer().append(" " + childText);
			} else if (DEBUG)
				System.out.println("DOING NOTHING WITH: "
						+ getChildText(node, genericizeNumbers));

		} else if (nodeName.equals("img")) {
			pp.setNumImages(pp.getNumImages() + 1);
		} else {
			int numNodes = list == null ? 0 : list.getLength();
			for (int i = 0; i < numNodes; i++) {
				Node childNode = list.item(i);
				followNode(childNode, pp, genericizeNumbers, careAboutAds, snippetSize,
						indent + "  ");
			}
		}
	}

	public RawContent getRawContent(URL url, String cookies) throws IOException {
		try {
			URLConnection conn = url.openConnection();
			
			if (StringUtils.isNotEmpty(cookies)) 
				conn.addRequestProperty("Cookie", cookies);
			
			String encoding = conn.getContentEncoding();
			if (StringUtils.isEmpty(encoding))
				encoding = "UTF-8";

			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream(), encoding));
			String line;
			StringBuffer buf = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				buf.append(line);
				buf.append(NL);
			}
			RawContent rc = new RawContent();
			rc.rawContent = buf.toString();
			rc.encoding = encoding;
			return (rc);
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	public static class RawContent {
		public String rawContent;
		public String encoding;
	}

	public ParsedPage getContent(CachedURL url, ParsedPage pp,
			boolean careAboutAds, int snippetSize) throws IOException {
		return (newParse(url, false, careAboutAds, false, snippetSize, pp));
	}

	private boolean setupNextLine() throws IOException {
		curLine = reader.readLine();

		if (DEBUG)
			System.out.println("CURLINE:" + curLine);

		if (curLine == null) {
			curLineLower = null;
			return (false);
		} else {
			curLine = curLine.trim();

//			if (genericizeNumbers)
//				curLine = curLine.replaceAll(TIME_EXPR, TIME_TOKEN).replaceAll(
//						MONEY_EXPR, MONEY_TOKEN)
//						.replaceAll(NUM_EXPR, NUM_TOKEN);

			curLineLower = curLine.toLowerCase();
		}

		return (true);
	}

	private void substring(int start) {
		curLine = curLine.substring(start);
		curLineLower = curLineLower.substring(start);
	}

	/**
	 * How this should work: determine unbalanced elements. try to remove them.
	 * then reparse assuming well formed?
	 * 
	 * @param url
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	// This really needs to be rewritten pretty soon... or does it? handle
	// ill-formed content well...
	private ParsedPage getContentInt(CachedURL url, boolean genericizeNumbers,
			boolean careAboutAds, ParsedPage pp) throws IOException {

		this.genericizeNumbers = genericizeNumbers;
		Date start = new Date();
		boolean hasAds = false;
		boolean hasPopups = false;
		String title = null;
		String description = null;
		String[] keywords = null;
		ArrayList<String> headlinesArr = new ArrayList<String>();
		HashSet<String> linksSet = new HashSet<String>();

		int numImages = 0;
		String snippet = null;
		boolean inJavaScript = false;
		boolean inStyle = false;
		boolean inBody = false;
		int contentSize = 0;
		boolean inTag = false;
		StringBuffer buf = new StringBuffer();

		while (setupNextLine()) {
			contentSize += curLine.length();
			int javaScriptIdx = curLineLower.indexOf("<script");
			if (javaScriptIdx != -1)
				inJavaScript = true;
			int styleIdx = curLineLower.indexOf("<style");
			if (styleIdx != -1)
				inStyle = true;

			if ((inJavaScript && curLineLower.indexOf("window.open") != -1)
					|| curLineLower.indexOf("javascript:window.open") != -1)
				hasPopups = true;

			if ((javaScriptIdx == -1 && curLineLower.indexOf("</script") != -1)
					|| (javaScriptIdx != -1 && curLineLower.indexOf("</script",
							javaScriptIdx + 1) != -1)) {
				inJavaScript = false;
				continue;
			}

			if ((styleIdx == -1 && curLineLower.indexOf("</style") != -1)
					|| (styleIdx != -1 && curLineLower.indexOf("</style",
							styleIdx + 1) != -1)) {
				inStyle = false;
				continue;
			}

			if (curLineLower.indexOf("<body") != -1)
				inBody = true;
			if (curLineLower.indexOf("</body") != -1)
				inBody = false;

			int imgIdx = curLineLower.indexOf("<img src");
			// TODO: extract alt text / title on images
			if (imgIdx != -1) {
				numImages++;
				String altText = tryToExtractAttributeOneLine("alt", imgIdx);
				if (StringUtils.isNotEmpty(altText)) {
					buf.append(" ");
					buf.append(altText);
					buf.append(" ");
				}
			}

			if (careAboutAds) {

				if (!hasAds && curLine.length() > 0) {
					initAdList();
					for (String s : AD_LIST)
						if (curLine.indexOf(s) != -1) {
							hasAds = true;
							break;
						}
				}
			}

			String link = tryToExtractMultiLine("a");
			if (StringUtils.isNotEmpty(link))
				linksSet.add(link);

			if (title == null)
				title = tryToExtractMultiLine("title");

			if (keywords == null)
				keywords = tryToExtractMetaArrayOneLine("keywords");

			if (description == null)
				description = tryToExtractMetaOneLine("description");

			String headline = tryToExtractMultiLine("h1");
			if (headline == null)
				headline = tryToExtractMultiLine("h2");
			if (headline == null)
				headline = tryToExtractMultiLine("h3");
			if (headline == null)
				headline = tryToExtractMultiLine("h4");

			if (StringUtils.isNotEmpty(headline))
				headlinesArr.add(headline);

			if (curLine == null)
				break;

			if (inStyle || inJavaScript)
				continue;

			boolean moreTags = false;
			do {
				if (inTag) {
					int closeTagIdx = curLine.indexOf('>');

					if (closeTagIdx != -1) {
						inTag = false;

						if (closeTagIdx == curLine.length() - 1)
							break;

						substring(closeTagIdx + 1);
					} else
						break;
				}

				int openTagIdx = curLine.indexOf('<');
				if (openTagIdx == -1) {
					moreTags = false;

					if (!StringUtils.isEmpty(curLine)) {
						if (buf.length() > 0)
							buf.append(NL);
						buf.append(curLine);
					}
				} else {
					moreTags = true;
					inTag = true;

					int snippetLength = snippet == null ? 0 : snippet.length();
					if (snippetLength < 100 && inBody && !inJavaScript) {
						String tmp = curLine.substring(0, openTagIdx);
						if (tmp.trim().length() > snippetLength)
							snippet = tmp;
					}

					String curLineSubstring = curLine.substring(0, openTagIdx);

					// If we're going to append something non-empty, and there's
					// a non-ws char
					// right before it, add ws:
					if (curLineSubstring.length() > 0) {
						if (buf.length() > 0
								&& !Character.isWhitespace(buf.charAt(buf
										.length() - 1)))
							buf.append(" ");
					}
					buf.append(curLineSubstring);
					if (openTagIdx == curLine.length() - 1) {
						// buf.append(NL);
						break;
					}

					substring(openTagIdx + 1);
				}
			} while (moreTags);

		}

		String rawContent = buf.toString();
		int strippedContentSize = rawContent.length();
		String[] headlines = null;
		if (headlinesArr.size() > 0) {
			headlines = new String[headlinesArr.size()];
			headlinesArr.toArray(headlines);
		}

		if (StringUtils.isEmpty(snippet)) {
			if (strippedContentSize > 200)
				snippet = rawContent.substring(0, 200) + "...";
			else
				snippet = rawContent;
			snippet = snippet.trim();
		} else if (snippet.length() > 200)
			snippet = snippet.substring(0, 200) + "...";

		Date end = new Date();
		System.out.println("Page parsing of " + url + " took: "
				+ (end.getTime() - start.getTime()) + " milliseconds.");
		if (pp == null)
			pp = new ParsedPage(rawContent, url.getURL(), contentSize,
					strippedContentSize, title, description, keywords, hasAds,
					hasPopups, linksSet, numImages, snippet, headlines);
		else
			pp.init(rawContent, url.getURL(), contentSize, strippedContentSize,
					title, description, keywords, hasAds, hasPopups, linksSet,
					numImages, snippet, headlines);

		return (pp);
	}

	public static String safe(String s) {
		return (s == null ? "" : s);
	}

	public static String tryToExtractOneLine(String name, String line,
			String lowerLine) {
		String nameStr = "<" + name + ">";

		int nameIdx = lowerLine.indexOf(nameStr);
		if (nameIdx == -1)
			return (null);
		String endNameStr = "</" + name + ">";
		int endNameIdx = lowerLine.indexOf(endNameStr);
		if (endNameIdx == -1)
			return (null);

		return (line.substring(nameIdx + nameStr.length(), endNameIdx));
	}

	private int advanceUntil(int startIdx, String stopper, StringBuffer buf)
			throws IOException {

		int stopperIdx = curLineLower.indexOf(stopper, startIdx);
		if (stopperIdx == -1) {
			// it appears to be multi-line:
			while (setupNextLine()) {
				stopperIdx = curLineLower.indexOf(stopper);
				if (stopperIdx != -1) {
					if (buf != null)
						paddToBuf(buf, curLine.substring(0, stopperIdx));

					// substring(stopperIdx);
					return (stopperIdx + stopper.length());
				} else if (buf != null) {
					paddToBuf(buf, curLine);
				}
			}
		} else if (buf != null)
			paddToBuf(buf, curLine.substring(startIdx, stopperIdx
					+ stopper.length()));
		return (stopperIdx + stopper.length());
	}

	private void paddToBuf(StringBuffer buf, String s) {
		if (buf.length() > 0
				&& !Character.isWhitespace(buf.charAt(buf.length() - 1)))
			buf.append(" ");
		buf.append(s);
	}

	public String tryToExtractMultiLine(String name) throws IOException {
		if (curLineLower == null) {
		
			if (curLine != null)
				System.out.println ("HEY WHY is there a descrepancy here: " + curLine + " NAME: " + name);
			return (null);
		}
		String nameStr = "<" + name;
		String altNameStr = "<" + name + ">";

		int nameIdx = curLineLower.indexOf(nameStr);
		int closerIdx = nameIdx + nameStr.length();
		if (nameIdx == -1
				|| curLineLower.length() > nameIdx + nameStr.length()
				&& (!Character.isWhitespace(curLineLower.charAt(nameIdx
						+ nameStr.length())))) {
			nameIdx = curLineLower.indexOf(altNameStr);
			closerIdx = nameIdx + altNameStr.length();
		} else {
			// int closer = curLineLower.indexOf(">", nameIdx +
			// nameStr.length());
			StringBuffer buf = new StringBuffer();
			closerIdx = advanceUntil(nameIdx + nameStr.length(), ">", buf);
			if (closerIdx == -1)
				return (null);
			// nameStr = curLineLower.substring(nameIdx, closer + 1);
			// nameStr = buf.toString();
			// nameIdx = closer;
		}
		if (nameIdx == -1)
			return (null);
		String endNameStr = "</" + name + ">";
		int endNameIdx = curLineLower.indexOf(endNameStr, closerIdx);
		if (endNameIdx == -1) {
			StringBuffer buf = new StringBuffer(curLine.substring(closerIdx));
			// it appears to be multi-line:
			while (setupNextLine()) {
				endNameIdx = curLineLower.indexOf(endNameStr);
				if (endNameIdx != -1) {
					paddToBuf(buf, curLine.substring(0, endNameIdx));
					substring(endNameIdx);
					break;
				} else
					paddToBuf(buf, curLine);
			}
			return (stripHtml(buf));
		} else {
			try {
				StringBuffer buf = new StringBuffer(curLine.substring(
						closerIdx, endNameIdx));
				substring(endNameIdx);
				return (stripHtml(buf));
			} catch (StringIndexOutOfBoundsException e) {
				e.printStackTrace();
				// System.out.println ("What is going on???");
			}
			return ("");
		}
	}

	public static String stripHtml(StringBuffer buf) {
		boolean insideTag = false;
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < buf.length(); i++) {
			char c = buf.charAt(i);

			if (c == '<')
				insideTag = true;
			else if (c == '>')
				insideTag = false;
			else {
				if (!insideTag)
					ret.append(c);
			}
		}
		return (ret.toString());
	}

	/**
	 * <meta name="keywords" content="New York Times, international news, daily newspaper, national, politics, science, business, your money, AP breaking news, business technology, technology, Cybertimes, circuits, new york times, navigator, sports, weather, editorial, Op-Ed, arts and leisure, film, movie reviews, theater, stock quotes, arts, classified ads, automobiles, books, crossword puzzle, job market, help wanted, careers, real estate listings, travel, web glossary, new york region, Navigator, cybertimes, op-ed, job listings, forums, business connections, theatre reviews, auto classifieds, newspaper archives, travel forecasts, NY Yankees, Mets, Giants, Jets, boxing, pro football scores, major league baseball, college basketball, Knicks, Rangers, Islanders, college football, sports commentary, fashion and style, Hockey, tennis, major league soccer, global issues, associated press, regional news coverage, quick news, women's health, obituaries, stock quotes, charts, market indexes, sports update, politics, science, political news, science times"
	 * />
	 */
	public String tryToExtractMetaOneLine(String name) {
		if (curLineLower == null)
			return (null);
		
		int metaIdx = curLineLower.indexOf("<meta");
		if (metaIdx == -1)
			return (null);

		String nameStr = "name=\"" + name + "\"";

		int nameIdx = curLineLower.indexOf(nameStr, metaIdx);
		if (nameIdx == -1)
			return (null);

		int contentIdx = curLineLower.indexOf("content=\"", nameIdx);
		if (contentIdx == -1)
			return (null);

		int endQuoteIdx = curLineLower.indexOf("\"", contentIdx + 10);
		if (endQuoteIdx == -1)
			return (null);

		return (curLineLower.substring(contentIdx + 9, endQuoteIdx));
	}

	/**
	 * Example: <img src="blah.png" alt="Blah blah"/>
	 **/
	public String tryToExtractAttributeOneLine(String name, int startIdx) {
		int nameIdx = curLineLower.indexOf(name + "=\"", startIdx);
		if (nameIdx == -1)
			return (null);

		if (curLineLower.length() <= nameIdx + name.length() + 3)
			return (null);

		int endQuoteIdx = curLineLower.indexOf("\"", nameIdx + name.length()
				+ 3);
		if (endQuoteIdx == -1)
			return (null);

		return (curLine.substring(nameIdx + name.length() + 2, endQuoteIdx));
	}

	public String[] tryToExtractMetaArrayOneLine(String name) {
		String val = tryToExtractMetaOneLine(name);

		if (val == null)
			return (null);

		StringTokenizer tok = new StringTokenizer(val, ", ");
		int count = tok.countTokens();
		String[] valArr = new String[count];
		int counter = 0;
		while (tok.hasMoreTokens())
			valArr[counter++] = tok.nextToken();
		return (valArr);
	}

	public static Message []	receiveMail () throws Exception {
		String host = "TODO GET POP3 SERVER";
		String username = "gotamatch";
		String password = "Jeff5Mike";

		// Create empty properties
		Properties props = new Properties();

		// Get session
		Session session = Session.getDefaultInstance(props, null);

		// Get the store
		Store store = session.getStore("pop3");
		store.connect(host, username, password);

		// Get folder
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);

		// Get directory
		Message message[] = folder.getMessages();

		//for (int i=0, n=message.length; i<n; i++) {
		//   System.out.println(i + ": " + message[i].getFrom()[0] 
		//     + "\t" + message[i].getSubject());
		//}

		// Close connection 
		folder.close(false);
		store.close();
		
		return (message);
	}
	
	public static void sendMail(String subject, String msgText, String from,
			String to) throws Exception {
		Properties props = System.getProperties();
		// -- Attaching to default Session, or we could start a new one --
		props.put("mail.smtp.host", "k2smtpout.secureserver.net");
		Session session = Session.getDefaultInstance(props, null);
		// -- Create a new message --
		Message msg = new MimeMessage(session);
		// -- Set the FROM and TO fields --
		if (!StringUtils.isEmpty(from))
			msg.setFrom(new InternetAddress(from));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to,
				false));
		// -- We could include CC recipients too --
		// if (cc != null)
		// msg.setRecipients(Message.RecipientType.CC
		// ,InternetAddress.parse(cc, false));
		// -- Set the subject and body text --
		msg.setSubject(subject);
		// msg.setText(msgText);
		msg.setContent(msgText, "text/html");
		// -- Set some other header information --
		// msg.setHeader("X-Mailer", "LOTONtechEmail");
		msg.setSentDate(new Date());
		// -- Send the message --
		Transport.send(msg);
	}

	public static boolean xequals(Object o1, Object o2) {
		if (o1 == null)
			return (o2 == null);
		else if (o2 == null)
			return (o1 == null);

		return (o1.equals(o2));
	}

	public static void main(String[] args) throws Exception {

		new WebUtils().newParse(new CachedURL(args[0]), true, false, false, 1024, null);
		// System.out.println("content: " + new WebUtils().getContent(new
		// URL(args [0]), null, true));
	}
}
