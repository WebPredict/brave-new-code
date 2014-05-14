package wp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import wp.model.User;
import wp.service.UserService;

/**
 * @struts.action path="/ImageUpload" validate="false"
 *   name="uploadForm" scope="request" input="/EditUser.do"
 * @struts.action-forward name="success" path="/EditUser.do"
 * @spring.bean name="/ImageUpload"
 * 
 * @spring.property name="userService" ref="userService"
 * @author Jeff
 *
 */
public class UploadAction extends Action
{
	public static final int	MAX_FILE_SIZE = 2 << 19;
	
	private UserService	userService;
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		UploadForm myForm = (UploadForm)form;
		String	userId = (String)request.getSession().getAttribute("userId");
	    
        // Process the FormFile
        FormFile myFile = myForm.getTheFile();
        String contentType = myFile.getContentType();
        String fileName    = myFile.getFileName();
        int fileSize       = myFile.getFileSize();
        
        if (fileSize > MAX_FILE_SIZE) {
        	ActionMessages	errors = getErrors(request);
        	errors.add("theFile", new ActionMessage("error.fileTooLarge", new Object [] {fileSize, MAX_FILE_SIZE}));
			saveErrors(request, errors);
        	return (mapping.getInputForward());
        }
        else if (fileSize == 0 || StringUtils.isEmpty(fileName)) {
        	ActionMessages	errors = getErrors(request);
        	errors.add("theFile", new ActionMessage("error.noFile"));
			saveErrors(request, errors);
        	return (mapping.getInputForward());
        }
        
        byte[] fileData    = myFile.getFileData();
	    //System.out.println("contentType: " + contentType);
	    //System.out.println("File Name: " + fileName);
	    //System.out.println("File Size: " + fileSize);

	    String	tag = myForm.getTag();
	    User	user = userService.findUser(userId);
	    userService.addPhoto(user, fileData, fileName, tag);
	    return mapping.findForward("success");
  }
} 