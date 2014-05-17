package wp.core;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Tidy;

public class TidyParser {
	final static StringBuffer sb = new StringBuffer();
	final static List<String> headingList = new ArrayList<String>();

	public enum ContentType {TITLE, HEADING, LINK, CONTENT, DESCRIPTION, KEYWORDS, ABSTRACT, META};

	public String getRawContent(URL url) throws IOException {

		Tidy tidy = new Tidy();
		URLConnection	conn = url.openConnection();
		String	encoding = conn.getContentEncoding();
		if (StringUtils.isEmpty(encoding))
			encoding = "UTF-8";

		tidy.setXmlOut(true);
		Document d = tidy.parseDOM(conn.getInputStream(), null);

		sb.setLength(0);
		headingList.clear();

		NodeList list = d.getElementsByTagName("title");
		if (list.getLength() > 0){
			Node node = list.item(0).getFirstChild();
			if (node instanceof Text){
				Text text = (Text) node;
				sb.append("title:");
				String content = text.getNodeValue();
				content = content.replaceAll(" and ","");
				sb.append(content);
				text.setNodeValue("");
			}

		}

		list = d.getElementsByTagName("meta");
		if (list.getLength() > 0){
			int numItems = list.getLength();
			for (int n=0; n < numItems; n++){
				Element e = (Element) list.item(n);
				String name = e.getAttribute("name");
				if (!StringUtils.isBlank(name)){
					String content = e.getAttribute("content");
					if (!StringUtils.isBlank(content)){
						sb.append("\n");
						sb.append(name);
						sb.append(":");
						content = content.replaceAll(" and ","");
						sb.append(content);

					}
				}
			}
		}

		getElement(d, d.getDocumentElement());

		for (String heading: headingList)
			System.out.println(heading);

		System.out.println(sb.toString());

		return "";
	}


	private void getElement(Document doc, Element e){
		if (e.hasChildNodes()){
			NodeList l = e.getChildNodes();
			int numItems = l.getLength();

			for (int n = 0; n < numItems; n++)
			{
				Node node = l.item(n);
				if (node instanceof Element)
				{
					Element element = (Element) node;
					if ("script".equalsIgnoreCase(element.getTagName()))
						element.setNodeValue("");
					else
						getElement(doc, element);

				} else if (node instanceof Text)
				{
					Text text = (Text) node;
					String content = text.getNodeValue();
					content = (content != null && content.length() > 0) ? content.trim(): "";
					if ( content.length() > 0){
						String name = text.getParentNode().getNodeName();
						int numChars = name.length();
						// This is questionable, since it is not necessarily a meaningful heading at the page level
						if (numChars == 2 && name.toLowerCase().startsWith("h")){
							boolean isHeading = Character.isDigit(name.charAt(numChars-1));
							if (isHeading)
								headingList.add(name.concat(":").concat(text.getNodeValue()));
						} else {
							sb.append(text.getParentNode().getNodeName());
							sb.append(":");
							sb.append(text.getNodeValue());
							sb.append("\n");
						}
					}
				}

			}

		}
	}


	public static void main (String [] args) throws Exception {

		String test = new TidyParser().getRawContent(new URL(args [0]));
	}

}
