package wp.dao;

import java.sql.SQLException;
import java.util.List;

import wp.model.CrawlerImpl;
import wp.model.CrawlerResult;
import wp.model.User;

public interface CrawlerDao {

	void	save (CrawlerResult r) throws SQLException;

	void	save (CrawlerImpl r) throws SQLException;

	void	runCrawlersForAll () throws SQLException;
	
	void runCrawlersFor(User user) throws SQLException;

	CrawlerImpl	findById (Long id) throws SQLException;

	void	deleteResults (CrawlerImpl crawler, List<CrawlerResult> results) throws SQLException;
}
