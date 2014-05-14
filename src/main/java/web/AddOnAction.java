package wp.web;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import wp.core.Rater;
import wp.core.WebUtils;
import wp.model.CategorySet;
import wp.model.User;
import wp.service.UserService;

/**
 * @struts.action path="/AddOnGen" validate="false"
 *   name="userForm" scope="request" input="/FinishSignup.do"
 * @struts.action-forward name="success" path="/Welcome.do"
 * @spring.bean name="/AddOnGen" 
 * 
 * @spring.property name="userService" ref="userService"
 * @author Jeff
 *
 */
public class AddOnAction extends Action { 

	private UserService	userService;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		ServletOutputStream out = response.getOutputStream(); 

		// edit the browser.xul file according to menu options
		// edit the preferences for userid
		String	userId = request.getParameter("userId");
		if (StringUtils.isEmpty(userId))
			userId = (String)request.getSession().getAttribute("userId");
		User	user = userService.findUser(userId);

		if (user == null) {
			// TODO complain properly here
			request.setAttribute("systemerror", "You need to be logged in for this action");
			return (mapping.getInputForward());
		}

		BufferedReader	br = null;
		try { 
			String		addOnDir = Rater.getTheRater().getDataDir();
			ZipFile		addOn = new ZipFile(addOnDir + "/linkratertemplate.xpi");
			ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
			ZipOutputStream zout = new ZipOutputStream(bout); 
			zout.setLevel(6);
            
			CategorySet	cs = user.getCategorySet();
			List<String>	cats = cs.getCategories();

			Enumeration	entries = addOn.entries();
			while (entries.hasMoreElements()) {
				ZipEntry		entry = (ZipEntry)entries.nextElement();
				InputStream		is = addOn.getInputStream(entry);
				String			entryName = entry.getName();
				if (entryName.equals("chrome/content/browser.xul")) {					
					StringBuffer	buf = new StringBuffer();
					int	counter = 1;
					for (String cat : cats) {
						buf.append("<menuitem id=\"link-rater-" + String.valueOf(counter++) + 
								"\" label=\"" + cat + "\" oncommand=\"linkRater.rate('" + cat + "')\" />");
						buf.append(WebUtils.NL);
					}
					
					replace(entry, is, zout, new String [] {"TEMPLATE_AREA"}, new String [] {buf.toString()});
				}
				else if (entryName.equals("chrome/content/linkRater.js")) {
					replace(entry, is, zout, new String [] {"REPLACE_USER_ID_HERE", "REPLACE_SERVER_URL_HERE"}, 
							new String [] {userId, Rater.getTheRater().getServerName() + 
							getServlet().getServletContext().getServletContextName()}); 
				}
				else {
					ZipEntry	clonedEntry = new ZipEntry(entryName);
					CRC32 crc = new CRC32();
				    byte []	buffer = new byte [30000];
					int	bytesRead = is.read(buffer);					
					
					if (bytesRead != -1) {
						clonedEntry.setSize(bytesRead);
			            crc.reset();
			            crc.update(buffer);
			            clonedEntry.setCrc(crc.getValue());
					}
					zout.putNextEntry(clonedEntry);

					if (bytesRead != -1) {
			            zout.write(buffer, 0, bytesRead);					
						zout.flush();
					}
				}
				
				zout.closeEntry(); 
			}
			
			zout.finish(); 
			zout.close();

			response.setContentType("application/zip"); 
			response.setHeader("Content-Disposition","attachment; filename=linkrater.xpi;"); 
			byte []	bytes = bout.toByteArray();
			response.setContentLength(bytes.length);
			out.write(bytes);
			out.flush(); 
		} 
		catch (Exception e) 
		{ 
			request.setAttribute("systemerror", e.toString());
			return (mapping.getInputForward());
		}
		finally {
			if (br != null)
				br.close();
		}
		return mapping.findForward("success");
	} 
	
	private static void	replace (ZipEntry entry, InputStream is, ZipOutputStream zout, String [] from, String [] to) throws Exception {
		BufferedReader	br = null;
		try {
		
			br = new BufferedReader(new InputStreamReader(is));
			StringBuffer	templateBuf = new StringBuffer();
			String			next;
			while ((next = br.readLine()) != null) {
				templateBuf.append(next);
				templateBuf.append(WebUtils.NL);
			}
			String	templateText = templateBuf.toString();
			
			for (int i = 0; i < from.length; i++)
				templateText = templateText.replaceAll(from [i], to [i]);
			
			ZipEntry	newEntry = new ZipEntry(entry.getName());
			CRC32 crc = new CRC32();
			
		    byte []	buffer = templateText.getBytes();
		    newEntry.setSize(buffer.length);
			crc.reset();
			crc.update(buffer);
		    newEntry.setCrc(crc.getValue());
			zout.putNextEntry(newEntry);
			zout.write(buffer, 0, buffer.length);					
			zout.flush();
		}
		finally {
			if (br != null)
				br.close();
		}
	}
}