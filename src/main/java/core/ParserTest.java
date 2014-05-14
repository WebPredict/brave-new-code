package wp.core;

import junit.framework.TestCase;
import wp.model.ParsedPage;

public class ParserTest extends TestCase {

	private WebUtils	utils;
		
	public void	testSimpleHtml () throws Exception {
		StringBuffer	buf = new StringBuffer("<div class=\"searchColumn\">");
		buf.append(WebUtils.NL);
		buf.append("<p style=\"font:bold 1.1em Arial; margin:0 0 10px 0\">Find the best job in the New York metro area and beyond.</p>");
		buf.append(WebUtils.NL);
		buf.append("<form class=\"searchForm\" action=\"http://nytimes.monster.com/Search.aspx\" method=\"get\" name=\"advJobsearchForm\">");
		buf.append(WebUtils.NL);
		buf.append("<INPUT type=\"hidden\" name=\"cy\" value=\"us\" />");
		buf.append(WebUtils.NL);
//		buf.append("<title>this is a title</title>");
//		buf.append(WebUtils.NL);
//		buf.append("</head>");
//		buf.append(WebUtils.NL);
//		buf.append("<body>what will happen to this");
//		buf.append(WebUtils.NL);
//		buf.append("</body>");
//		buf.append(WebUtils.NL);
//		buf.append("</html>");
		
		CachedURL	cached = null; // TODO
		ParsedPage	pp = utils.newParse(cached, true, true, false, 255, null);
		assertTrue(pp.getTitle().equals("this is a title"));
		assertTrue(pp.getStrippedContentSize() == 38);
		//assertTrue(pp.getContentSize() == 14);
		
	}
	
	public void	testHuffPost () throws Exception {
		ParsedPage	pp = utils.getContent(new CachedURL("file:///C:/WebPredictCache/www.huffingtonpost.com"), null, true, 255);
		assertTrue(pp.getTitle().equals("Breaking News and Opinion on The Huffington Post"));
	}
	
	protected void	setUp () {
		utils = new WebUtils();
	}
	
}
