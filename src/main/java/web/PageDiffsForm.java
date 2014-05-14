package wp.web;

import org.apache.struts.action.ActionForm;

/**
 * @struts.form name="pageDiffsForm"
 * @author Jeff
 *
 */
public class PageDiffsForm extends ActionForm {

	private String reference;
	private String	page1;
	private String	page2;
	private String	page3;
	private String	page4;
	private String	page5;
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getPage1() {
		return page1;
	}
	public void setPage1(String page1) {
		this.page1 = page1;
	}
	public String getPage2() {
		return page2;
	}
	public void setPage2(String page2) {
		this.page2 = page2;
	}
	public String getPage3() {
		return page3;
	}
	public void setPage3(String page3) {
		this.page3 = page3;
	}
	public String getPage4() {
		return page4;
	}
	public void setPage4(String page4) {
		this.page4 = page4;
	}
	public String getPage5() {
		return page5;
	}
	public void setPage5(String page5) {
		this.page5 = page5;
	}
	
	
}
