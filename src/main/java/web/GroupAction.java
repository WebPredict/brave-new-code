package wp.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import wp.core.AlreadyExistsException;
import wp.model.Group;
import wp.model.User;
import wp.service.GroupService;

/**
 * @struts.action path="/ListGroups" parameter="listGroups" validate="false"
 *   name="groupsForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listGroups" path="/ListGroups.jsp"
 * @spring.bean name="/ListGroups"
 * @struts.action-forward name="unauthorized" path="/Unauthorized.jsp"
 * 
 * @struts.action path="/DeleteGroup" parameter="deleteGroup" validate="false"
 *   name="groupsForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listGroups" path="/ListGroups.jsp"
 * @spring.bean name="/DeleteGroup"
 * 
 * @struts.action path="/EditGroup" parameter="editGroup" validate="false"
 *   name="groupForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="editGroup" path="/GroupEdit.jsp"
 * @spring.bean name="/EditGroup"
 * 
 * @struts.action path="/ViewGroup" parameter="viewGroup" validate="false"
 *   name="groupForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="viewGroup" path="/GroupView.jsp"
 * @spring.bean name="/ViewGroup"
 * 
 * @struts.action path="/SaveGroup" parameter="saveGroup" validate="false"
 *   name="groupForm" scope="request" input="/EditGroup.do"
 * @struts.action-forward name="saveGroup" path="/GroupEdit.jsp"
 * @spring.bean name="/SaveGroup"
 * 
 * @struts.action path="/AddToGroup" parameter="addToGroup" validate="false"
 *   name="groupForm" scope="request" input="/ViewGroup.do"
 * @spring.bean name="/AddToGroup"
 *  
 * @struts.action path="/RemoveFromGroup" parameter="removeFromGroup" validate="false"
 *   name="groupForm" scope="request" input="/ViewGroup.do"
 * @spring.bean name="/RemoveFromGroup"
 *  
 * @spring.property name="groupService" ref="groupService"
 * 
 * @author Jeff
 *
 */
public class GroupAction extends BaseAction {

	private GroupService	groupService;
	
	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public ActionForward listGroups(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		List<Group>	groups = groupService.listGroups(null);
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		request.setAttribute("groups", groups);
		return (mapping.findForward("listGroups"));
	}
	
	public ActionForward editGroup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		GroupForm	gf = (GroupForm)form;
		String	groupId = request.getParameter("id");
		
		if (!StringUtils.isEmpty(groupId)) {
			Group	group = groupService.findById(new Long(groupId));
			gf.bind(group);
		}
		else {
			ArrayList<String>	members = new ArrayList<String>();
			members.add(user.getUserId());
			gf.setMembers(members);
			gf.setOwnerId(user.getUserId());
		}
		return (mapping.findForward("editGroup"));
	}

	public ActionForward viewGroup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String	groupId = request.getParameter("groupId");
		Group	group = groupService.findById(new Long(groupId));

		User	user = getUserFromSession(request);

		if (user != null) {
			request.setAttribute("canAddToGroup", !group.getMembers().contains(user));
			request.setAttribute("canRemoveFromGroup", group.getMembers().contains(user));
		}

		request.getSession().setAttribute("group", group);
		return (mapping.findForward("viewGroup"));
	}
	
	public ActionForward addToGroup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		String	groupId = request.getParameter("id");
		Group	group = groupService.findById(new Long(groupId));
		
		userService.addGroup(user, group.getId());
		
		request.getSession().setAttribute("group", group);
		return (mapping.findForward("viewGroup"));
	}

	public ActionForward removeFromGroup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		String	groupId = request.getParameter("id");
		Group	group = groupService.findById(new Long(groupId));
		
		userService.removeGroup(user, group.getId());
		
		request.getSession().setAttribute("group", group);
		return (mapping.findForward("viewGroup"));
	}
	

	public ActionForward saveGroup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{	
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		GroupForm	gf = (GroupForm)form;
		Group	group = gf.extract(groupService);
		String	id = group.getGroupId();
		String	name = group.getName();
		String	ownerId = group.getOwnerId();
		boolean	isNew = false;
		
		if (StringUtils.isEmpty(id)) {
			id = name;
			isNew = true;
			group.setGroupId(id);			
		}
		
		ActionMessages	errors = getErrors(request);
		if (StringUtils.isEmpty(id)) {
			errors.add("id", new ActionMessage("error.invalidId", id));
			saveErrors(request, errors);
		}
		
		if (StringUtils.isEmpty(ownerId)) {
			errors.add("ownerId", new ActionMessage("error.invalidUserId", ownerId));
			saveErrors(request, errors);
		}
		
		if (errors.size() > 0) {
			return (mapping.getInputForward());
		}
		
		try {
			groupService.save(group);
		}
		catch (AlreadyExistsException e) {
			errors.add("groupId", new ActionMessage("error.groupAlreadyExists", group.getGroupId()));
			saveErrors(request, errors);
			return mapping.getInputForward();
		}	
		if (isNew) {
			User	owner = userService.findUser(ownerId);
			userService.addGroup(owner, group.getId());
		}
		request.setAttribute("group", group);
		return (mapping.findForward("viewGroup"));
	}
}
