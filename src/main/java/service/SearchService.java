package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.core.SearchResult;
import wp.dao.SearchInfo;

public interface SearchService {

	List<SearchResult>	find (SearchInfo info) throws SQLException;
}
