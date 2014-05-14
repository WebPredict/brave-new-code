package wp.dao;

import java.sql.SQLException;

import wp.model.RatedPage;
import wp.model.UserStats;

public interface UserStatsDao {

	void		delete (UserStats stats, RatedPage rp) throws SQLException;
	void		save (UserStats stats) throws SQLException;
	UserStats	findStatsFor (String userId) throws SQLException;
}
