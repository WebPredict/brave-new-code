package wp.model;

import wp.utils.Utils;

/**
 * Contains a link, the link text, and the immediate context of the link
 **/
public class LinkInfo {

	private String	link;
	private String	text;
	private String	context;
	
	public LinkInfo () { }
	
	public boolean	equals (Object o) {
		if (!(o instanceof LinkInfo))
			return (false);
		
		LinkInfo	other = (LinkInfo)o;
		return (Utils.xequals(other.getLink(), link) && Utils.xequals(other.getText(), text) && 
				Utils.xequals(other.getContext(), context));
	}
	
	public int	hashCode () {
		int	code = 0;
		if (link != null)
			code += link.hashCode();
		if (text != null)
			code += text.hashCode();
		if (context != null)
			code += context.hashCode();
		return (code);
	}
	
	public LinkInfo (String link, String text, String context) {
		this.link = link;
		this.text = text;
		this.context = context;
	}
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	
	
	
}
