package wp.service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import wp.core.AlreadyExistsException;
import wp.dao.GroupDao;
import wp.dao.ListInfo;
import wp.model.Group;
import wp.model.User;

/**
 * @spring.bean id="groupService"
 * @spring.property name="groupDao" ref="groupDao"
 * @author Jeff
 *
 */
public class GroupServiceImpl implements GroupService {

	private GroupDao	groupDao;
	
	public GroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public Group create(String groupId, String name, String ownerId, String description, Collection<User> members) 
	throws AlreadyExistsException, SQLException {
		return (groupDao.create(groupId, name, ownerId, description, members));
	}

	public void delete(Group group) throws SQLException {
		groupDao.delete(group);
	}

	public Group findById(Long groupId) throws SQLException {
		return (groupDao.findById(groupId));
	}

	public void save(Group group) throws SQLException, AlreadyExistsException {
		groupDao.save(group);
	}

	public List<Group>	listGroups (ListInfo info) throws SQLException {
		return (groupDao.listGroups(info));
	}
	
	public int	countGroups (ListInfo info) throws SQLException {
		return (groupDao.countGroups(info));
	}
}
