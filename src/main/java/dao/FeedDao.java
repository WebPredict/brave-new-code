package wp.dao;

import java.sql.SQLException;

import wp.model.Feed;

public interface FeedDao {

	Feed	findById (Long id) throws SQLException;

	Feed	findByUserAndName (Long userId, String name) throws SQLException;

	void	save (Feed feed) throws SQLException;
}
