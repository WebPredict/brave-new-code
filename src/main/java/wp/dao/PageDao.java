package wp.dao;

import java.sql.SQLException;
import java.util.List;

import wp.model.ParsedPage;
import wp.model.RatedPage;

public interface PageDao {
	
	List<RatedPage>	findRatedPages (ListInfo info) throws SQLException;
	
	List<RatedPage>	findPublicRatedPages (ListInfo info) throws SQLException;
	
	List<String>	findPublicRatedPageURLs (String notBy) throws SQLException;

	ParsedPage		findParsedPageBYURL (String url) throws SQLException;
	
	int				countRatedPages (ListInfo info) throws SQLException;
}
