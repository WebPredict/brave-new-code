package wp.dao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.core.CachedURL;
import wp.core.Utils;
import wp.core.WebUtils;
import wp.model.Alert;
import wp.model.Crawler;
import wp.model.CrawlerImpl;
import wp.model.CrawlerResult;
import wp.model.LinkInfo;
import wp.model.User;

/**
 * @spring.bean id="crawlerDao"
 * @spring.property name="userStatsDao" ref="userStatsDao"
 * @spring.property name="userDao" ref="userDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class CrawlerDaoImpl extends HibernateDaoSupport implements CrawlerDao {

	private UserStatsDao	userStatsDao;
	private UserDao			userDao;
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserStatsDao getUserStatsDao() {
		return userStatsDao;
	}

	public void setUserStatsDao(UserStatsDao userStatsDao) {
		this.userStatsDao = userStatsDao;
	}

	public static final long	MAX_TIME_TO_WAIT = 30000l;
	
	@Transactional(readOnly=false)
	public void	crawl (final Alert alert) throws SQLException {
			WebUtils	wu = new WebUtils();
			String	urlStr = Utils.normalizeUrlStr(alert.getUrlStr());
			try {
				List<LinkInfo>	links = wu.extractLinks (new CachedURL(urlStr), false, true);
				
				// TEMPORARY FOR TESTING:
				int	maxLinks = 50;
				int	counter = 0;
				
				for (LinkInfo link : links) {
					if (++counter > maxLinks)
						break; // TEMPROARY
					
					try {
						long	start = System.currentTimeMillis();
						new CachedURL(link.getLink()).getContent(); // force fetching to cache it
						
						alert.getCrawledUrls().add(link.getLink());
						long	end = System.currentTimeMillis();
						
						long	timeToWait = end - start > 1000 ? (10 * (end - start)) : 10000l;
						
						if (timeToWait > MAX_TIME_TO_WAIT)
							timeToWait = MAX_TIME_TO_WAIT;
						
						// Sleep for 10 * the time it took to fetch content, min 10 seconds
						Thread.sleep(timeToWait);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				alert.setLastCrawled(new Date());				
				getHibernateTemplate().save(alert);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
	}
	
	public CrawlerImpl	findById (Long id) throws SQLException {
		return (CrawlerImpl)getHibernateTemplate().get(CrawlerImpl.class, id);
	}


	@Transactional(readOnly=false)
	public void	deleteResults (CrawlerImpl crawler, List<CrawlerResult> results) throws SQLException {
		ArrayList<CrawlerResult>	toRemove = new ArrayList<CrawlerResult>();
		for (CrawlerResult result : results) 
			toRemove.add(result);
		
		for (CrawlerResult result : toRemove) {
			crawler.getCrawledResults().remove(result);
			getHibernateTemplate().delete(result);
		}
		getHibernateTemplate().save(crawler);
	}

	@Transactional(readOnly=false)
	public void	runCrawlersForAll () throws SQLException {
		try {
		List<User>	users = userDao.listUsers(null);
		
		if (users == null)
			return;
		
		for (User user : users) {
			List<CrawlerImpl>	crawlers = user.getCrawlersList();
			if (crawlers == null)
				continue;
			
			for (CrawlerImpl crawler : crawlers) {
				if (crawler.isEnabled())
					crawler.asyncCrawl();
			}
		}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Transactional(readOnly=false)
	public void	save(CrawlerResult r) throws SQLException {
		getHibernateTemplate().save(r);
	}
	
	@Transactional(readOnly=false)
	public void	save(CrawlerImpl ci) throws SQLException {
		List<CrawlerResult>	results = ci.getCrawledResults();
		if (results != null) {
			for (CrawlerResult result : results) {
				getHibernateTemplate().save(result);
			}
		}
		
		getHibernateTemplate().save(ci);
		
	}
	
	@Transactional(readOnly=false)
	public void	runCrawlersFor(User user) throws SQLException {
				
		List<CrawlerImpl>	crawlers = user.getCrawlersList();
	
		if (crawlers == null)
			return;
	
		for (CrawlerImpl ci : crawlers) {
			if (ci.isEnabled())
				ci.asyncCrawl();
		}
	}

}
