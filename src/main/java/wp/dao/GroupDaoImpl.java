package wp.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import wp.core.AlreadyExistsException;
import wp.model.Group;
import wp.model.User;

/**
 * @spring.bean id="groupDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class GroupDaoImpl extends QueryConstructor implements GroupDao {

	@Transactional(readOnly = false)
	public Group create(String groupId, String name, String ownerId,
			String description, Collection<User> members)
			throws AlreadyExistsException, SQLException {
		
		if (findByGroupId(groupId) != null)
			throw new AlreadyExistsException("Group", groupId);
		Group	group = new Group();
		group.setGroupId(groupId);
		group.setName(name);
		group.setDescription(description);
		group.setOwnerId(ownerId);
		group.setMembers(members);
		save(group);
		return (group);
		
	}

	@Transactional(readOnly = false)
	public void delete(Group group) throws SQLException {
		getHibernateTemplate().save(group);
	}

	public Group findByGroupId(String groupId) throws SQLException {
		List<Group>	groups = getHibernateTemplate().find("from Group where groupId=?", groupId);
		return (groups == null || groups.size() == 0 ? null : groups.get(0));
	}

	public Group findById(Long id) throws SQLException {
		return (Group)getHibernateTemplate().get(Group.class, id);
	}

	public List<Group> listGroups(ListInfo info) throws SQLException {
		return ((List<Group>)makeQuery(info, "Group"));
	}

	public int	countGroups (ListInfo info) throws SQLException {
		return (makeCountQuery(info, "Group"));
	}
	
	@Transactional(readOnly = false)
	public void save(Group group) throws SQLException, AlreadyExistsException {
		if (group.getId() == null) {
			if (findByGroupId(group.getGroupId()) != null)
				throw new AlreadyExistsException("Group", group.getGroupId());
		}
		
		getHibernateTemplate().save(group);
	}

}
