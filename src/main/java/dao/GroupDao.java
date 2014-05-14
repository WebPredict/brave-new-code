package wp.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import wp.core.AlreadyExistsException;
import wp.model.Group;
import wp.model.User;

public interface GroupDao {

	Group	findById (Long groupId) throws SQLException;
	
	Group	create (String groupId, String name, String ownerId, String description, Collection<User> members) 
		throws AlreadyExistsException, SQLException;
	
	void	delete (Group group) throws SQLException;
	
	void	save (Group group) throws SQLException, AlreadyExistsException;
	
	List<Group>	listGroups (ListInfo info) throws SQLException;
	
	int	countGroups (ListInfo info) throws SQLException;
}
