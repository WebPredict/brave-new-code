package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.model.CrawlerImpl;
import wp.model.CrawlerResult;
import wp.model.User;

public interface CrawlerService {

	void 	runCrawlersFor(User user) throws SQLException;

	void	runCrawlersForAll () throws SQLException;
	
	void	deleteResults (CrawlerImpl crawler, List<CrawlerResult> results) throws SQLException;
	
	void	save (CrawlerImpl crawler) throws SQLException;
	
	void	save (CrawlerResult r) throws SQLException;
	
}
