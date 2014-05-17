package wp.web;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * @struts.form name="uploadForm"
 * @author Jeff
 *
 */
public class UploadForm extends ActionForm
{
	  private FormFile theFile;
	  private String	tag;
	  
	  public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	/**
	   * @return Returns the theFile.
	   */
	  public FormFile getTheFile() {
	    return theFile;
	  }
	  /**
	   * @param theFile The FormFile to set.
	   */
	  public void setTheFile(FormFile theFile) {
	    this.theFile = theFile;
	  }
	} 