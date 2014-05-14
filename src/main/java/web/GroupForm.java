package wp.web;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;

import wp.model.Group;
import wp.service.GroupService;

/**
 * @struts.form name="groupForm"
 * @author Jeff
 *
 */
public class GroupForm extends ActionForm {

	private String	groupId;
	private String	id;
	private String	name;
	private String	ownerId;
	private String	description;
	private List<String>	members;
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void	bind (Group group) {
		id = group.getId() == null ? "" : group.getId().toString();
		description = group.getDescription();
		groupId = group.getGroupId();
		ownerId = group.getOwnerId();
		name = group.getName();
	}
	
	public Group	extract (GroupService groupService) throws SQLException {
		
		Group	group;
		if (StringUtils.isEmpty(id)) {
			group = new Group();
		}
		else 
			group = groupService.findById(new Long(id));
		
		group.setOwnerId(ownerId);
		group.setDescription(description);
		group.setName(name);
		group.setGroupId(groupId);
		
		return (group);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
