package wp.dao;

import java.sql.SQLException;
import java.util.List;

import wp.model.UserConfig;

public interface UserConfigDao {

	UserConfig	findByName (String name) throws SQLException;
	
	void	save (UserConfig config) throws SQLException;
	
	void	delete (UserConfig config) throws SQLException;
	
	List<UserConfig>	list (String filter) throws SQLException;

}
