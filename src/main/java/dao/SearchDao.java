package wp.dao;

import java.sql.SQLException;
import java.util.List;

import wp.core.SearchResult;

public interface SearchDao {

	List<SearchResult>	find (SearchInfo info) throws SQLException;
}
