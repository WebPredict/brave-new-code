package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.dao.CrawlerDao;
import wp.model.CrawlerImpl;
import wp.model.CrawlerResult;
import wp.model.User;

/**
 * @spring.bean id="crawlerService"
 * @spring.property name="crawlerDao" ref="crawlerDao"
 * @author Jeff
 *
 */
public class CrawlerServiceImpl implements CrawlerService {

	private CrawlerDao	crawlerDao;
	
	public CrawlerDao getCrawlerDao() {
		return crawlerDao;
	}

	public void setCrawlerDao(CrawlerDao crawlerDao) {
		this.crawlerDao = crawlerDao;
	}

	public void	runCrawlersForAll () throws SQLException {
		this.crawlerDao.runCrawlersForAll();
	}

	public void	save (CrawlerImpl r) throws SQLException {
		this.crawlerDao.save(r);
	}

	public void	save (CrawlerResult r) throws SQLException {
		this.crawlerDao.save(r);
	}

	public void runCrawlersFor(User user) throws SQLException {
		crawlerDao.runCrawlersFor(user);
	}

	public void	deleteResults (CrawlerImpl crawler, List<CrawlerResult> results) throws SQLException {
		crawlerDao.deleteResults(crawler, results);
	}


}
